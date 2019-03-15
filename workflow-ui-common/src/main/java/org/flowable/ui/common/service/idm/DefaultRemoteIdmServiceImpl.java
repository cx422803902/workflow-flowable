package org.flowable.ui.common.service.idm;

import java.util.ArrayList;
import java.util.List;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.Token;
import org.flowable.idm.api.User;
import org.flowable.ui.common.model.RemoteGroup;
import org.flowable.ui.common.model.RemoteToken;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.service.exception.NotFoundException;
import org.flowable.ui.common.idm.model.UserInformation;
import org.flowable.ui.common.idm.service.GroupService;
import org.flowable.ui.common.idm.service.TokenService;
import org.flowable.ui.common.idm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
//TODO 默认不较用用户
@Service
public class DefaultRemoteIdmServiceImpl implements RemoteIdmService {

    @Autowired
    protected UserService userService;
    @Autowired
    protected TokenService tokenService;
    @Autowired
    protected GroupService groupService;

    @Override
    public RemoteUser authenticateUser(String username, String password) {
        return getUserInfoMation(username);
    }

    @Override
    public RemoteToken getToken(String tokenValue) {
        return getRemoteToken(tokenValue);
    }

    @Override
    public RemoteUser getUser(String userId) {
        return getUserInfoMation(userId);
    }

    @Override
    public List<RemoteUser> findUsersByNameFilter(String filter) {
        return findUsersByFilter(filter);
    }

    @Override
    public List<RemoteUser> findUsersByGroup(String groupId) {
        //TODO
        return new ArrayList<>();
    }

    @Override
    public RemoteGroup getGroup(String groupId) {
        return getGroupInformation(groupId);
    }

    @Override
    public List<RemoteGroup> findGroupsByNameFilter(String filter) {
        return findGroupsByFilter(filter);
    }

    private RemoteUser getUserInfoMation(String userId) {
        UserInformation userInformation = userService.getUserInformation(userId);
        if (userInformation != null) {
            User user = userInformation.getUser();
            RemoteUser remoteUser = new RemoteUser();
            remoteUser.setId(user.getId());
            remoteUser.setFirstName(user.getFirstName());
            remoteUser.setLastName(user.getLastName());
            remoteUser.setFullName((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""));
            remoteUser.setEmail(user.getEmail());

            if (userInformation.getGroups() != null) {
                for (Group group : userInformation.getGroups()) {
                    RemoteGroup remoteGroup = new RemoteGroup();
                    remoteGroup.setId(group.getId());
                    remoteGroup.setName(group.getName());
                    remoteGroup.setType(group.getType());
                    remoteUser.getGroups().add(remoteGroup);
                }
            }

            if (userInformation.getPrivileges() != null) {
                for (String privilege : userInformation.getPrivileges()) {
                    remoteUser.getPrivileges().add(privilege);
                }
            }
            return remoteUser;
        }
        else {
            throw new NotFoundException();
        }
    }

    private List<RemoteUser> findUsersByFilter(String filter) {
        List<User> users = userService.getUsers(filter, null, null);
        List<RemoteUser> remoteUsers = new ArrayList<>(users.size());
        for (User user : users) {
            RemoteUser remoteUser = new RemoteUser();
            remoteUser.setId(user.getId());
            remoteUser.setFirstName(user.getFirstName());
            remoteUser.setLastName(user.getLastName());
            remoteUser.setFullName((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""));
            remoteUser.setEmail(user.getEmail());
            remoteUsers.add(remoteUser);
        }
        return remoteUsers;
    }

    private RemoteToken getRemoteToken(String tokenId) {
        Token token = tokenService.findTokenById(tokenId);
        if (token == null) {
            throw new NotFoundException();
        }
        else {
            RemoteToken remoteToken = new RemoteToken();
            remoteToken.setId(token.getId());
            remoteToken.setValue(token.getTokenValue());
            remoteToken.setUserId(token.getUserId());
            return remoteToken;
        }
    }

    public RemoteGroup getGroupInformation(String groupId) {
        Group group = groupService.getGroup(groupId);
        if (group != null) {
            RemoteGroup remoteGroup = new RemoteGroup();
            remoteGroup.setId(group.getId());
            remoteGroup.setName(group.getName());
            remoteGroup.setType(group.getType());
            return remoteGroup;

        }
        else {
            throw new NotFoundException();
        }
    }

    public List<RemoteGroup> findGroupsByFilter(String filter) {
        List<RemoteGroup> result = new ArrayList<>();
        List<Group> groups = groupService.getGroups(filter);
        for (Group group : groups) {
            RemoteGroup remoteGroup = new RemoteGroup();
            remoteGroup.setId(group.getId());
            remoteGroup.setName(group.getName());
            remoteGroup.setType(group.getType());
            result.add(remoteGroup);
        }
        return result;
    }
}
