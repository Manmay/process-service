app.controller("TaskCtrl", function($scope, $routeParams, $http, $location){
	
	$scope.task;

	$scope.changed = function(){
		$scope.task.updated = true;
	};
	
	$scope.init = function(){
		console.log($routeParams.taskId);
		$http({
			url: '/api/tasks/' + $routeParams.taskId + '?userId=' + $routeParams['userId'],
			method: "GET",
			headers: {
				'Accept': 'application/json'
			}
		}).success(function(data, status){
			console.log('success');
			$scope.task = data;
		}).error(function(error){
			console.log("error");
		});
	};
	
	$scope.claim = function(){
		$http({
			url: '/api/tasks/' + $routeParams.taskId + '?userId=' + $routeParams['userId'],
			method: "PATCH"
		}).success(function(data, status){
			console.log("success!!!");
			$scope.task.assignee = $routeParams['userId'];
		}).error(function(error){
			console.log("error");
		});
	};
	
	$scope.save = function(){
		$http({
			url: '/api/tasks/' + $routeParams.taskId + "?userId=" + $routeParams['userId'],
			method: "PUT",
			headers: {
				'Content-Type': 'application/json'
			},
			data: $scope.task.data
		}).success(function(data, status){
			console.log("success!!!");
			$location.path("/tasks");
		}).error(function(error){
			console.log("error");
		});
	};
	
	$scope.submit = function(){
		$http({
			url: '/api/tasks/' + $routeParams.taskId + "?userId=" + $routeParams['userId'],
			method: "POST",
			headers: {
				'Content-Type': 'application/json'
			},
			data: $scope.task.data
		}).success(function(data, status){
			console.log("success!!!");
			$location.path("/tasks");
		}).error(function(error){
			console.log("error");
		});
	};



});


app.controller('RegistrationReviewCtrl', function($scope, $controller) {

	$controller('TaskCtrl', {$scope: $scope});

});

app.controller("RegistrationVerifyCtrl", function($scope, $controller){

	$controller('TaskCtrl', {$scope: $scope});
	
});