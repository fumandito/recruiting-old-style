package it.f2informatica.core.gateway;

import it.f2informatica.core.model.AuthenticationModel;
import it.f2informatica.core.model.RoleModel;
import it.f2informatica.core.model.UpdatePasswordModel;
import it.f2informatica.core.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryGateway {

  AuthenticationModel authenticationByUsername(String username);

  boolean updatePassword(UpdatePasswordModel request);

  UserModel findUserById(String userId);

  UserModel findByUsername(String username);

  UserModel findByUsernameAndPassword(String username, String password);

  Page<UserModel> findAllExcludingCurrentUser(Pageable pageable, String usernameToExclude);

  Iterable<UserModel> findUsersByRoleName(String roleName);

  UserModel saveUser(UserModel userModel);

  boolean updateUser(UserModel userModel);

  void deleteUser(String userId);

  Iterable<RoleModel> loadRoles();

  RoleModel findRoleByName(String roleName);

  boolean isCurrentPasswordValid(String userId, String currentPwd);
}