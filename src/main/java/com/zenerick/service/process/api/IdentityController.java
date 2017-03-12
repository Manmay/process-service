package com.zenerick.service.process.api;

import java.util.ArrayList;
import java.util.List;


import com.zenerick.schema.process.*;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class IdentityController {
	
	
	@Autowired
	IdentityService identityService;
	
	@Autowired
	ModelMapper mapper;
	
	
	@RequestMapping(path="api/users", method=RequestMethod.GET, produces="application/json")
	public List<UserQueryMultiple> findUsers(){
		List<UserQueryMultiple> response = new ArrayList<UserQueryMultiple>();
		List<User> users = identityService.createUserQuery().list();
		for(User user : users){
			response.add(mapper.map(user, UserQueryMultiple.class));
		}
		return response;
	}
	
	@RequestMapping(path="api/users/{userId}", method=RequestMethod.GET, produces="application/json")
	public UserQuerySingle findUser(@PathVariable("userId") String userId) {
		User user = identityService.createUserQuery().userId(userId).singleResult();
		if(user == null){
			throw new IllegalArgumentException("User Not Found");
		}
		UserQuerySingle response  = mapper.map(user, UserQuerySingle.class);
		
		List<Group> groups = identityService.createGroupQuery().groupMember(userId).list();
		for(Group group : groups){
			response.getGroups().add(mapper.map(group, UserQuerySingle.Groups.class ));
		}
		return response;
	}
	
	@RequestMapping(path="api/users", method=RequestMethod.POST, consumes="application/json")
	public void createUser(@RequestBody UserCommandCreate request){
		User user = identityService.newUser(request.getId());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		identityService.saveUser(user);
	}
	
	@RequestMapping(path="api/users/{userId}", method=RequestMethod.PUT, consumes="application/json")
	public void updateUser(@PathVariable("userId") String userId, @RequestBody UserCommandUpdate request){
		User user = identityService.createUserQuery().userId(userId).singleResult();
		if(user == null){
			throw new IllegalArgumentException("User Not Found");
		}
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		identityService.saveUser(user);
	}
	
	@RequestMapping(path="api/users/{userId}/memebership/create", method=RequestMethod.PATCH)
	public void createMemebership(@PathVariable("userId")  String userId,  @RequestParam("groupId") String[] groupIds){
		for(String groupId : groupIds){
			identityService.createMembership(userId, groupId);
		}
	}
	
	@RequestMapping(path="api/users/{userId}/memebership/delete", method=RequestMethod.PATCH)
	public void deleteMemebership(@PathVariable("userId")  String userId,  @RequestParam("groupId") String[] groupIds){
		for(String groupId : groupIds){
			identityService.deleteMembership(userId, groupId);
		}
	}
	
	
	@RequestMapping(path="api/groups", method=RequestMethod.POST, consumes="application/json")
	public void createGroup(@RequestBody GroupCommandCreate request){
		Group group = identityService.newGroup(request.getId());
		group.setName(request.getName());
		group.setType(request.getType());
		identityService.saveGroup(group);
	}
	
	@RequestMapping(path="api/groups", method=RequestMethod.GET, produces="application/json")
	public List<GroupQueryMultiple> findGroups(){
		List<GroupQueryMultiple> response = new ArrayList<GroupQueryMultiple>();
		List<Group> groups = identityService.createGroupQuery().list();
		for(Group group: groups){
			response.add(mapper.map(group, GroupQueryMultiple.class));
		}
		return response;
	}
	
	@RequestMapping(path="api/groups/{groupId}", method=RequestMethod.GET, produces="application/json")
	public GroupQuerySingle findGroup(@PathVariable("groupId") String groupId){
		Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
		if(group == null){
			throw new IllegalArgumentException("No Group Found");
		}
		GroupQuerySingle response = mapper.map(group, GroupQuerySingle.class);
		List<User> users = identityService.createUserQuery().memberOfGroup(groupId).list();
		for(User user : users){
			response.getUsers().add(mapper.map(user, GroupQuerySingle.Users.class));
		}
		return response;
	}
	
	@RequestMapping(path = "api/prepare", method=RequestMethod.GET)
	public void prepare(){
		GroupCommandCreate group = new GroupCommandCreate();
		group.setId("broker");
		group.setName("Broker");
		group.setType("user");
		this.createGroup(group);
		
		UserCommandCreate user1 = new UserCommandCreate();
		user1.setId("manmay");
		user1.setFirstName("Manmay");
		user1.setLastName("Mohanty");
		user1.setEmail("manmay@gmail.com");
		user1.setPassword("secret");
		this.createUser(user1);
		
		UserCommandCreate user2 = new UserCommandCreate();
		user2.setId("mrunmay");
		user2.setFirstName("Mrunmay");
		user2.setLastName("Mohanty");
		user2.setEmail("manmay@gmail.com");
		user2.setPassword("secret");
		this.createUser(user2);
		
		this.createMemebership("manmay", new String[]{"broker"});
		this.createMemebership("mrunmay", new String[]{"broker"});
		
	}

}
