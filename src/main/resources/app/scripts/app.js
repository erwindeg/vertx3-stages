'use strict';

/**
 * @ngdoc overview
 * @name resourcesApp
 * @description
 * # resourcesApp
 *
 * Main module of the application.
 */
angular
  .module('resourcesApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/views/main.html',
        controller: 'MainCtrl'
      })
      .when('/success', {
        templateUrl: 'app/views/success.html',
        controller: ''
      })
      .when('/applications', {
        templateUrl: 'app/views/applications.html',
        controller: 'ApplicationsCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
