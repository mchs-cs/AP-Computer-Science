/**
 * This is override code that allows us to inject an Authorization header
 * into all XMLHttpRequests such that they will have OAuth2 access tokens.
 */

/**
 * Root function to call.  This function overrides the send method
 * and adds an Authorization header to the request.
 */
(function() {
  // First save off the original send function.
  XMLHttpRequest.prototype.realSend = XMLHttpRequest.prototype.send;

  // Second, redefine the send function.
  var newSend = function(vData) {
    this.setRequestHeader(
        'Authorization',
        'Bearer %(OAUTH2_HEADER_VALUE)s');
    this.realSend(vData);
  };

  // And finally set the function override.
  XMLHttpRequest.prototype.send = newSend;

})();
