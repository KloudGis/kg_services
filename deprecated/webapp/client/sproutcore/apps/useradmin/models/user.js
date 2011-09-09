// ==========================================================================
// Project:   Useradmin.User
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Useradmin */

/** @class

  (Document your Model here)

  @extends SC.Record
  @version 0.1
*/
Useradmin.User = SC.Record.extend(
/** @scope Useradmin.User.prototype */ {

	email: SC.Record.attr(String),
    fullName: SC.Record.attr(String),
    password: SC.Record.attr(String),
    location: SC.Record.attr(String),
	isSuperUser: SC.Record.attr(Boolean),
	isActive: SC.Record.attr(Boolean),
	
	label: function(){
		if(SC.none(this.get('fullName'))){
			return "%@".fmt(this.get('email'));	
		}else{
			return "%@ (%@)".fmt(this.get('email'), this.get('fullName'));		
		}
	}.property('email', 'fullName').cacheable(),
	
	
	iconSuper: function(){
		if(this.get('isSuperUser')){
			return 'sc-icon-group-16';
		}else{
			return 'sc-icon-user-16';
		}
	}.property('isSuperUser').cacheable(),
	
	iconActive: function(){
		if(this.get('isActive')){
			return sc_static('images/active.png');
		}else{
			return sc_static('images/not_active.png');
		}
	}.property('isActive').cacheable()
	

}) ;
