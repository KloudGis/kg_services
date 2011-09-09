
Useradmin.ProfileView = SC.View.extend({
	
	layerId: 'profile-view',
	
	childViews: 'emailView fullNameView locationView cieView'.w(),
	
	
	emailView: Useradmin.InputView.design({
		
		attributeName: 'email',
		attributeLabel: '_email'.loc(),
		spellCheckEnabled: NO,
		layout:{top:0, left: 20, right: 20, height: 50}
	}),

	fullNameView: Useradmin.InputView.design({
		
		attributeName: 'fullName',
		attributeLabel: '_fullName'.loc(),
		spellCheckEnabled: NO,
		layout:{top:70, left: 20, right: 20, height: 50}
	}),
	
	locationView: Useradmin.InputView.design({
		
		attributeName: 'location',
		attributeLabel: '_location'.loc(),
		spellCheckEnabled: NO,
		layout:{top:140, left: 20, right: 20, height: 50}
	}),
	
	cieView: Useradmin.InputView.design({
		
		attributeName: 'compagny',
		attributeLabel: '_compagny'.loc(),
		spellCheckEnabled: NO,
		layout:{top:210, left: 20, right: 20, height: 50}
	}),
 	
});