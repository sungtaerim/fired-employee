package com.lepse.employee.dao;

import com.lepse.employee.models.Employee;
import com.lepse.employee.models.Surrogate;
import com.lepse.employee.service.*;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2007_01.DataManagement;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.services.strong.query._2006_03.SavedQuery;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.Property;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.ImanQuery;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;
import com.teamcenter.soa.exceptions.NotLoadedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public class EmployeeDAO {

    private ServiceData serviceData;
    private DataManagementService dataManagementService;
    private final EmployeeCredentialManager credentialManager;

    private final String searchName = "__WEB_find_user";
    private final String searchParam = "User ID";
    private final String active = "0";
    private final String notActive = "1";

    @Autowired
    public EmployeeDAO(EmployeeCredentialManager credentialManager) {
        this.credentialManager = credentialManager;
    }

    /**
     * Get TC user with ImanQuery by user id
     * @param userId user ID
     * @return Employee
     * */
    public Employee getEmployee(String userId, String status) {
        Connection connection = new Connection(credentialManager.getTcServer(), credentialManager, "REST", "HTTP");

        ImanQuery query = null;

        SavedQueryService queryService = SavedQueryService.getService(connection);
        dataManagementService = DataManagementService.getService(connection);
        try {
            SavedQuery.GetSavedQueriesResponse savedQueries = queryService.getSavedQueries();
            if (savedQueries.queries.length == 0) {
                System.out.println("There are no saved queries in the system");
                return new Employee();
            }

            for (int i = 0; i < savedQueries.queries.length; i++) {
                if (savedQueries.queries[i].name.equals(searchName)) {
                    query = savedQueries.queries[i].query;
                    break;
                }
            }
        } catch (ServiceException e) {
            System.out.println("GetSavedQueries service request failed");
            System.out.println(e.getMessage());
            return new Employee();
        }

        if (query == null) {
            System.out.println("There is not an " + searchName + " query");
            return new Employee();
        }

        try {
            com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput[] savedQueryInput = new com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput[1];
            savedQueryInput[0] = new com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput();
            savedQueryInput[0].query = query;
            savedQueryInput[0].entries = new String[1];
            savedQueryInput[0].values = new String[1];
            savedQueryInput[0].entries[0] = searchParam;
            savedQueryInput[0].values[0] = userId;
            savedQueryInput[0].maxNumToReturn = 25;

            com.teamcenter.services.strong.query._2007_09.SavedQuery.SavedQueriesResponse savedQueryResult = queryService.executeSavedQueries(savedQueryInput);
            com.teamcenter.services.strong.query._2007_09.SavedQuery.QueryResults found = savedQueryResult.arrayOfResults[0];

            int length = found.objectUIDS.length;

            String[] uids = new String[length];
            System.arraycopy(found.objectUIDS, 0, uids, 0, length);

            if (uids.length == 0) {
                return null;
            }

            serviceData = (ServiceData) dataManagementService.loadObjects(uids);
            ModelObject[] foundObjs = new ModelObject[serviceData.sizeOfPlainObjects()];
            for (int i = 0; i < serviceData.sizeOfPlainObjects(); i++) {
                foundObjs[i] = serviceData.getPlainObject(i);
            }

            ModelObject modelEmployee = foundObjs[0];
            serviceData = dataManagementService.getProperties(new ModelObject[]{modelEmployee}, Properties.getEmployeeProperties());

            if (status != null && status.equals(active)) {
                // if user not active - not interested
                if (!modelEmployee.getPropertyDisplayableValue(Properties.status.name()).equals(active)) {
                    return null;
                }
            } else if (status != null && status.equals(notActive)) {
                // if user active - not interested
                if (modelEmployee.getPropertyDisplayableValue(Properties.status.name()).equals(active)) {
                    return null;
                }
            }

            Employee employee = setEmployeeProperties(modelEmployee);
            Property property = modelEmployee.getPropertyObject(Properties.inbox_delegate.name());
            ModelObject modelSurrogate = property.getModelObjectValue();

            ModelObject userInbox = getDates(modelEmployee);

            Surrogate surrogate = setSurrogateProperties(modelSurrogate, userInbox);
            employee.setSurrogate(surrogate);

            return employee;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Employee();
    }

    /**
     * new instance of Employee
     * @param modelObject ModelObject type user
     * @return Employee
     * */
    private Employee setEmployeeProperties(ModelObject modelObject) {
        try {
            Employee employee = new Employee();

            employee.setUid(modelObject.getUid());
            employee.setId(modelObject.getPropertyDisplayableValue(Properties.user_id.name()));
            employee.setName(modelObject.getPropertyDisplayableValue(Properties.user_name.name()));
            employee.setIsOut(modelObject.getPropertyDisplayableValue(Properties.is_out_of_office.name()));
            employee.setGroup(modelObject.getPropertyDisplayableValue(Properties.login_group.name()));
            employee.setLastLogin(modelObject.getPropertyDisplayableValue(Properties.last_login_time.name()));
            employee.setStatus(modelObject.getPropertyDisplayableValue(Properties.status.name()));

            return employee;
        } catch (NotLoadedException ex) {
            ex.printStackTrace();
        }
        return new Employee();
    }

    /**
     * new instance of Surrogate
     * @param modelObject ModelObject type user
     * @param userInbox ModelObject type user_inbox
     * @return Surrogate
     * */
    private Surrogate setSurrogateProperties(ModelObject modelObject, ModelObject userInbox) {
        Surrogate surrogate = new Surrogate();
        try {
            if (modelObject != null) {
                if (modelObject.getTypeObject().getName().equals("GroupMember")) {
                    modelObject = groupMemberToUser(modelObject);
                }
                surrogate.setUid(modelObject.getUid());
                surrogate.setUserId(modelObject.getPropertyDisplayableValue(Properties.user_id.name()));
                surrogate.setUserName(modelObject.getPropertyDisplayableValue(Properties.user_name.name()));

                if (userInbox != null) {
                    serviceData = dataManagementService.getProperties(new ModelObject[]{userInbox}, Properties.getUserInboxProperties());

                    surrogate.setEndDate(userInbox.getPropertyDisplayableValue(Properties.end_date.name()));
                    surrogate.setStartDate(userInbox.getPropertyDisplayableValue(Properties.start_date.name()));
                }
            }
        } catch (NotLoadedException ex) {
            ex.printStackTrace();
        }
        return surrogate;
    }

    /**
     * Get User from GroupMember
     * @param modelObject ModelObject type groupMember
     * @return modelObject User modelObject
     * */
    private ModelObject groupMemberToUser(ModelObject modelObject) {
        try {
            serviceData = dataManagementService.getProperties(
                    new ModelObject[]{modelObject}, new String[]{"the_user"});

            Property prop = modelObject.getPropertyObject("the_user");
            modelObject = prop.getModelObjectValue();

            serviceData = dataManagementService.getProperties(new ModelObject[]{modelObject}, Properties.getSurrogateProperties());
        } catch (NotLoadedException ex) {
            ex.printStackTrace();
        }
        return modelObject;
    }

    /**
     * Get start_date and end_date from user_inbox object
     * @param modelObject ModelObject type user
     * @return returns ModelObject type taskInbox
     * */
    private ModelObject getDates(ModelObject modelObject) {
        try {
            serviceData = dataManagementService.getProperties(
                    new ModelObject[]{modelObject}, new String[]{"taskinbox"});

            Property prop = modelObject.getPropertyObject("taskinbox");
            modelObject = prop.getModelObjectValue(); // type - TaskInbox

            // find TaskInbox where referenced
            DataManagement.WhereReferencedResponse response = dataManagementService.whereReferenced(new WorkspaceObject[] {(WorkspaceObject) modelObject}, 1);
            DataManagement.WhereReferencedOutput[] whereReferencedOutputs = response.output;
            for (DataManagement.WhereReferencedOutput output : whereReferencedOutputs) {
                DataManagement.WhereReferencedInfo[] whereReferencedInfos = output.info;
                for (DataManagement.WhereReferencedInfo info : whereReferencedInfos) {
                    WorkspaceObject object = info.referencer;
                    if (object.getTypeObject().getName().equals("User_Inbox")) {
                        return object;
                    }
                }
            }
        } catch (NotLoadedException ex) {
            ex.printStackTrace();
        }
        return modelObject;
    }
}
