'use strict';

/**
 * @ngdoc function
 * @name resourcesApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the resourcesApp
 */
angular.module('resourcesApp')
  .controller('MainCtrl', function ($scope,$resource,$location) {
	  $scope.assignments = [{name: "Java EE webshop", description: "Het bouwen van een webshop met Java EE 6."},{name: "Vertx stage applicatie", description: "Het herbouwen van de stage aanmeld applicatie met behulp van Vertx."}, {name: "Kenniskaarten", description: "Ombouwen van de kenniskaarten sheets in een webapplicatie."}];
		 $scope.submit = function(){
			 $resource('/api/application').save($scope.application);
			 $location.path('/success');
		 }
  });

angular.module('resourcesApp')
.controller('ApplicationsCtrl', function ($scope,$resource,$location) {
	  $scope.applications = $resource('/api/application').query();
	  
	  var eb = new vertx.EventBus('http://'+window.location.host+'/eventbus');
		 eb.onopen = function() {
		      eb.registerHandler('application.channel', function(message) {
		    	  $scope.applications = $resource('/api/application').query();

		      });

		    }
  });