(function () {
    'use strict';
    angular.module('a360').service('SessionService', SessionService);
    SessionService.$inject = ['$resource', '$q'];
    function SessionService($resource, $q) {
        var sessionService = {};

        sessionService.sendSession = function(sessionName, endDate,  participants) {
            var sessionResource = $resource('/servlet/a360/sessions/create');
            var deferred = $q.defer();
            sessionResource.save({'sessionName': sessionName, 'endDate': endDate, 'participantList': participants}).$promise.then(function(data) {
                deferred.resolve(data);
            }, function(response) {
                deferred.reject(response);
            });
            return deferred.promise;
        };

        sessionService.getActiveQuestions = function () {
            var url = '/servlet/a360/questions/active';
            var questionsResource = $resource(url);
            var deferred = $q.defer();
            questionsResource.query().$promise.then(
                function (data) {
                    deferred.resolve(data);
                }, function (response) {
                    deferred.reject(response);
                });
            return deferred.promise;
        };

        return sessionService;
    }
})();