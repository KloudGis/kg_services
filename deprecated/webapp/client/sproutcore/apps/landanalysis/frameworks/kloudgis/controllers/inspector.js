// ==========================================================================
// Project:   Kloudgis.inspectorController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Main controller for the inspector. Content is the active feature controller

  @extends SC.Object
*/
sc_require('views/inspector');
Kloudgis.inspectorController = SC.ObjectController.create(
/** @scope Kloudgis.inspectorController.prototype */
 {

    inspectorView: null,
	lastInspectorLayout: null,
    activeClass: null,
	activeFeatures: null,

    featureSelected: function(feature) {
        var arrayFeature = [];
        if (feature) {
            arrayFeature.push(feature);
        }
        this.featuresSelected(arrayFeature);
    },

    featuresSelected: function(featureArray) {
        if (featureArray && featureArray.length > 0) {
            this.selectedFeatureDidChanged(featureArray);
        } else {
            this.closeInspector();
        }
    },

    selectedFeatureDidChanged: function(featureArray) {
        var newInspector = NO;
        //main inspector view
        if (!this.inspectorView) {
            newInspector = YES;
            var view = Kloudgis.InspectorView.create();
            this.set('inspectorView', view);
			view.addObserver('layout', this, this.positionChanged);
        }
        //active feature
        var feature = featureArray.objectAt(0);
        //active feature controller
        var oldController = this.get('content');
        var controller = feature.get('featureController');
		//feature controller is mandatory
		if(!controller){
			return NO;
		}
        var observerAdded = NO;
        if (!SC.none(oldController)) {
            if (oldController !== controller) {
                oldController.set('isMaster', NO);
                oldController.removeObserver('selection', this, this.activeSelectionChanged);
                oldController.clearSelection();
            } else {
                observerAdded = YES;
            }
        }
        if (!observerAdded) {
            controller.addObserver('selection', this, this.activeSelectionChanged);			
        }
        this.set('content', controller);
        controller.set('isMaster', YES);
        controller.addFeatures(featureArray);
        feature = controller.get('activeFeature');
        //feature view
        var contentV = feature.get('inspectorViewClass');
        var panel = null;
        if (!SC.none(contentV) && (SC.none(this.activeClass) || this.activeClass !== contentV)) {
            this.set('activeClass', contentV);
            panel = contentV.create();
        }
        if (panel) {		
            if (!SC.none(this.inspectorView.contentView)) {
                this.inspectorView.contentView.destroy();
            }
			panel.addObserver('inspectorSize', this, this.featureViewSizeChanged);
            this.inspectorView.set('contentView', panel);
            if (newInspector) {
                this.inspectorView.disableAnimation();
                //graphic bug if anim at start
                this._adjustSizeInspectorView(panel.get('inspectorSize'));
                this.adjustPositionInspectorView();
                this.inspectorView.append();
            } else {
				this.inspectorView.append();
                this._adjustSizeInspectorAnim(panel.get('inspectorSize'));              
            }
        }
    },

    activeSelectionChanged: function() {
		//console.log('activeSelectionChanged');
        var activeController = this.get('content');
        if (!SC.none(activeController)) {
			this.set('activeFeatures', activeController.get('selection'));
			var feature = activeController.activeFeature();
			if(!SC.none(feature)){
            	Kloudgis.mapController.highlightSelectionFeature(feature);
			}else{
				Kloudgis.mapController.clearHighlightSelectionFeatures();
				this.closeInspector();
			}
        }
    },

	commitChanges: function(){
		if (!SC.none(this.inspectorView)) {
		//important: force loose focus to make latecommit textfield to commit.
			this.inspectorView.makeFirstResponder(null);
		}
		Kloudgis.store.commitRecords();
	},

    closeInspector: function() {
        if (!SC.none(this.inspectorView)) {
			this.commitChanges();
			this.set('lastInspectorLayout', null);
			this._doCloseInspector();
        }
    },

    _doCloseInspector: function() {
        //console.log('removing inspector!!');
        Kloudgis.inspectorController.inspectorView.remove();
        this.activeClass = null;
        this.inspectorView.contentView.destroy();
        if (this.get('content')) {
            this.get('content').clearSelection();
        }
    },

    _disableAnimInspector: function() {
        this.inspectorView.disableAnimation();
    },

    adjustPositionInspectorView: function() {
        var wsize = SC.RootResponder.responder.computeWindowSize();
        var w = this.inspectorView.get('layout').width;
        var h = this.inspectorView.get('layout').height;
        var left = wsize.width / 2 - w / 2;
        var top = wsize.height / 2 - h / 2;
        this.inspectorView.adjust({'left': left, 'top': top});
    },

    _adjustSizeInspectorView: function(contentLayout) {
        var w = contentLayout.width;
        var h = contentLayout.height;
		this.inspectorView.adjust({'width': w, 'height': h});
    },

	_adjustSizeInspectorAnim: function(contentLayout){
		this.inspectorView.enableAnimation();
        this._adjustSizeInspectorView(contentLayout);
		this.invokeLater('_disableAnimInspector',
        500);
	},

//no anim
	adjustSizeInspectorView: function(contentLayout, callback, callbackmethod) {
		//this.inspectorView.enableAnimation();
        this._adjustSizeInspectorView(contentLayout);
		this.inspectorView.disableAnimation();
		callbackmethod.call(callback);		
    },

	positionChanged: function(){
		this.set('lastInspectorLayout', this.inspectorView.get('layout'));
	},
	
	featureViewSizeChanged: function(){
		this._adjustSizeInspectorAnim(this.inspectorView.contentView.get('inspectorSize'));
	},

});
