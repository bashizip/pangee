/*
 * Copyright 2017 bashizip.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grew.process;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.identity.db.DbIdentityServiceProvider;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;

/**
 *
 * @author bashizip
 */
public class ProcessIdentityProvider extends DbIdentityServiceProvider {

    public ProcessIdentityProvider() {
        super();
    }

    @Override
    public void deleteMembership(String userId, String groupId) {
        super.deleteMembership(userId, groupId); 
    }

    @Override
    public void createMembership(String userId, String groupId) {
        super.createMembership(userId, groupId); 
    }

    @Override
    public void deleteGroup(String groupId) {
        super.deleteGroup(groupId); 
    }

    @Override
    public GroupEntity saveGroup(Group group) {
        return super.saveGroup(group); 
    }

    @Override
    public GroupEntity createNewGroup(String groupId) {
        return super.createNewGroup(groupId); 
    }

    @Override
    public void deleteUser(String userId) {
        super.deleteUser(userId); 
    }

    @Override
    public User saveUser(User user) {
        return super.saveUser(user); 
    }

}
