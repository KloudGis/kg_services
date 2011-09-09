// ==========================================================================
// Project:   Kloudgis.InspectorFeatureView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Super class for feature view

  @extends SC.View
*/
sc_require('views/toolbar_button')
sc_require('views/close_dialog_button')
Kloudgis.InspectorFeatureView = SC.View.extend(
/** @scope Kloudgis.InspectorFeatureView.prototype */
{
    isJoin: NO,
  	
	inspectorSize: {
		width: 350,
		height: 200
	},
	
    //the layout for the feature view
    featureLayout: {
        top: 40,
        left: 8,
        right: 8,
        bottom: 5
    },

	extraHeight: 0,
	
	featuretypeName: null,
	controller: function(){
		var ftname = this.get('featuretypeName');
		return Kloudgis.modelManager.getController(ftname);
	}.property('featuretypeName').cacheable(),
	
    classNames: 'picker-main-view'.w(),
    childViews: 'controlBar featureView bottomBar'.w(),
    controlBar: SC.View.design({
        layout: {
            top: 0,
            left: 0,
            right: 0,
            height: 40
        },
	    classNames: 'picker-control-bar'.w(),
        init: function() {
            sc_super();
            var joined = this.getPath('parentView.isJoin');
            var ctrl = this.getPath('parentView.controller');
			this.labelFt.bind('value', SC.Binding.from(".selectedFeatureCtrl.labelInspector", ctrl));			
            if (joined) {
                this.set('layout', {
                    top: 0,
                    left: 0,
                    right: 0,
                    height: 27
                });
                this.parentView.set('featureLayout', {
                    top: 28,
                    left: 0,
                    right: 0,
                    bottom: 11
                });
                this.remove.set('isVisible', NO);
				this.btnZoom.set('isVisible', NO);		
            } else {	 
	    		this.btnZoom.set('target', ctrl);				
            }
        },

        childViews: 'labelFt remove  btnZoom'.w(),

		labelFt: SC.LabelView.design({
            layout: {
                centerY: 0,
                right: 5,
                left: 85,
                height: 24
            },
			textAlign: null,
			fontWeight: null,
            classNames: 'tools-view-labels label-centered'.w()
        }),

        remove: Kloudgis.CloseDialogButtonView.design({
            layout: {
                right: -25,
                top: -20,
                width: 30,
                height: 30
            },		
            action: function() {
                Kloudgis.inspectorController.closeInspector();
            }
        }),

		btnZoom: SC.ButtonView.design({
            layout: {
                centerY: -3,
                left: 5,
                width: 80,
                height: 24
            },
			classNames: ['dark'],
            title: "_zoom".loc(),
			action: 'zoomFeature'
        })
    }),

    //default view to render the feature components
    //OVERRIDE to set the proper view
    featureView: SC.View.design({
        childViews: ['labelView'],
        labelView: SC.LabelView.design({
            layout: {
                centerX: 0,
                centerY: 0,
                width: 100,
                height: 24
            },
            value: 'NO VIEW!'
        })
    }),

	bottomBar: SC.View.design(SC.Animatable, {		
		transitions: {
			opacity: {
	            duration: 1.0,
				timing: SC.Animatable.TRANSITION_CSS_EASE_IN
	        },
		},
		
        layout: {
            bottom: 0,
            left: 0,
            right: 0,
            height: 30
        },
		
		childViews: 'btnPrevious labelPos btnNext'.w(),
		
		_controller: null,
		_extent: NO,
		
		isVisible: NO,
		
		init: function() {
            sc_super();
			this._controller = this.getPath('parentView.controller');
			this.labelPos.bind('value', SC.Binding.from(".countLabel", this._controller));				 
	    	this.btnPrevious.set('target', this._controller);	
		    this.btnNext.set('target', this._controller);			          
			this._controller.addObserver('needPages', this, this.needPagesChanged);//force re-check
			this.adjust('opacity', 0);
			this.needPagesChanged();
		},		

		destroy: function(){
			sc_super();
			this._controller.removeObserver('needPages', this, this.needPagesChanged);
		},
		
		needPagesChanged: function(){
			if(this._controller && this.parentView){
				var parent = this.parentView;				
				var need = this._controller.get('needPages');
				var currentSize = this.parentView.get('inspectorSize');
				if(need && !this._extent){
					parent.set('extraHeight', 30);
					parent.set('inspectorSize', {height: currentSize.height + 30, width:currentSize.width});
					this.set('isVisible', YES);					
					this.adjust('opacity', 1.0);
					this._extent = YES;					
					parent.featureView.adjust({bottom: 35});					
				}else if(!need && this._extent){
					parent.set('extraHeight', 0);
					parent.set('inspectorSize', {height: currentSize.height - 30, width:currentSize.width});				
					this.adjust('opacity', 0.0);
					this.invokeLater(function(){
						this.set('isVisible', NO);
						if(this.parentView && this.parentView.featureView){
							this.parentView.featureView.adjust({bottom: 5});
						}
					}, 500);
					this._extent = NO;
				}
			}
		},

		btnPrevious: SC.ButtonView.design({
            layout: {
                centerY: 0,
                left: 5,
                width: 80,
                height: 24
            },
			classNames: 'dark back'.w(),
            title: "_previous".loc(),
			action: 'previousFeature'
        }),

		labelPos: SC.LabelView.design({
            layout: {
                centerY: 3,
                right: 85,
                left: 85,
                height: 24
            },
			textAlign: null,
			fontWeight: null,
            classNames: 'label-inspector-position-master label-centered'.w()
        }),

		btnNext: SC.ButtonView.design({
            layout: {
                centerY: 0,
                right: 5,
                width: 80,
                height: 24
            },
			classNames: 'dark next'.w(),
            title: "_next".loc(),
			action: 'nextFeature'
        })
    })
});
