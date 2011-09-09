// ==========================================================================
// Project:   Kloudgis.ToolbarButtonView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

 Infinite progress view

  @extends SC.ImageView
*/
Kloudgis.ProgressLoopView = SC.ImageView.extend({
	
		theme: 'default',
	
		init:function(){
			sc_super();
			if(this.theme === 'gray'){
				this.value = sc_static('images/loading_gray.gif');
			}else{
				this.value = sc_static('images/loading.gif');
			}
		},	
		
        layout: {
            width: 16,
            height: 16,
        },      
        useImageCache: NO			
});