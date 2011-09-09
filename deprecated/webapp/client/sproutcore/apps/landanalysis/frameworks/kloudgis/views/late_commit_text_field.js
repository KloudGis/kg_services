// ==========================================================================
// Project:   Kloudgis.LateCommitTextFieldView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Textfield to commit changes on focus lost

  @extends SC.TextFieldView
*/
Kloudgis.LateCommitTextFieldView = SC.TextFieldView.extend({
	
	applyImmediately: false,
  /* 
    override the parent 'fieldValueDidChange' function. 
    Fire events only when user has done editing 
  */ 
  /*fieldValueDidChange: function(partialChange){ 
    //console.log(partialChange+'; '+this.getFieldValue()); 
    if(partialChange) return true; //user is still editing 
    else return sc_super(); 
  } */
});
