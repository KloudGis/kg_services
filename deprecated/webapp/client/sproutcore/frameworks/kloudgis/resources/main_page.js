// ==========================================================================
// Project:   Kloudgis - mainPage
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

// This page describes the main user interface for your application.
sc_require('views/late_commit_text_field')
sc_require('views/toolbar_button')
sc_require('views/picker_pane')
sc_require('views/layer')
sc_require('views/featuretype')
sc_require('views/feature')
sc_require('views/close_dialog_button')
sc_require('views/progress_loop')
Kloudgis.mainPage = SC.Page.extend({

    mainPane: SC.MainPane.design({

        childViews: 'topToolbar mapView'.w(),

        topToolbar: SC.ToolbarView.design({
            layout: {
                "top": 0,
                "left": 0,
                "right": 0,
                "height": 46
            },
            classNames: ['toolbar'],
            childViews: 'chartsButton buttonTools toolbarSearchText searchAnim buttonOptions mapControlView logonUser buttonUserPos'.w(),

            chartsButton:  SC.ButtonView.design({
                layout: {
                    top: 10,
                    height: 24,
                    left: 10,
                    width: 110,
                },
				classNames: 'dark'.w(),
				toolTip: "_goToChart".loc(),
                icon: sc_static('images/buttons/pie-chart.png'),
				title: '_chartPage'.loc(),
                action: function() {
                    SC.routes.set('location', 'charts');
                }
            }),

            buttonTools: Kloudgis.ToolbarButtonView.design({
                layout: {
                    top: 10,
                    height: 24,
                    right: 140,
                    width: 36,
                },
				classNames: 'button-24'.w(),
				buttonBehavior: SC.TOGGLE_BEHAVIOR,
				valueBinding: 'Kloudgis.ftToolsController.activated',
                icon: "sc-icon-tools-24",
				iconSelected: sc_static('images/buttons/tools_w.png'),
				toolTip: "_layersTooltip".loc()
            }),

			buttonUserPos: SC.ButtonView.design({
				layout:{
					top:10,
					height: 24,
					right: 200,
					width: 28
				},
				classNames: 'dark img-centered'.w(),
				hasIcon: YES,
				titleMinWidth: 0,
				icon: sc_static('locate.png'),
				toolTip: "_locateTooltip".loc(),
				action: function(){
					this.set('executing', YES);
					Kloudgis.mapController.zoomUserPosition();
					this.updateLayer();
				},
				isEnabledBinding: 'Kloudgis.mapController.isZoomToUserPosition',
				
				executing: NO,
				
				userLocationChanged: function(){
					this.set('executing', NO);
					this.updateLayer();
				}.observes('Kloudgis.mapController.currentPosition'),
				
				userLocationErrorChanged: function(){
					this.set('executing', NO);
					this.updateLayer();
				}.observes('Kloudgis.mapController.currentPositionError'),
				
				render: function(context, firstTime){
					sc_super();
					if(this.get('executing')){
						context.addClass('rotate-image');
					}
				}
			}),

            buttonOptions: Kloudgis.ToolbarButtonView.design({
                layout: {
                    centerY: 0,
                    height: 24,
                    left: 150,
                    width: 36,
                },
				classNames: 'button-24'.w(),
                icon: "sc-icon-options-24",
				iconSelected: sc_static('images/buttons/gear_w.png'),
				buttonBehavior: SC.TOGGLE_BEHAVIOR,
				toolTip: "_optionsTooltip".loc(),
				valueBinding: 'Kloudgis.optionsController.activated',
                selectedChanged: function() {
					if(this.get('isSelected')){
                    	Kloudgis.optionsController.showPicker(this);
					}
                }.observes('isSelected')
            }),

            mapControlView: SC.SegmentedView.design({
                layout: {
                    centerY: 0,
                    centerX: -250,
                    width: 300,
                    height: 24
                },
                valueBinding: 'Kloudgis.mapControlController.activeTool',
                items: [{
                    title: '_map_select'.loc(),
                    value: 'selection',
					tooltip: '_selectionToolTooltip'.loc()
                },
                {
                    title: '_map_edit'.loc(),
                    value: 'edit',
					tooltip: '_editToolTooltip'.loc()
                },
                {
                    title: '_map_create'.loc(),
                    value: 'create',
					tooltip: '_createToolTooltip'.loc()
                }],
                itemTitleKey: 'title',
                itemValueKey: 'value',
				itemToolTipKey: 'tooltip'
            }),

            //search field
            toolbarSearchText: SC.TextFieldView.design({
                classNames: ['search'],
                layout: {
                    top: 10,
                    centerX: 0,
                    width: 180,
                    height: 24
                },
                valueBinding: 'Kloudgis.toolbarSearchController.search',

                keyDown: function(evt) {
                    var ret = sc_super();
                    var which = evt.which;
                    //console.log('key down search:' + which);
                    if (which === 13 && !evt.isIMEInput) {
                        //ENTER pressed
                        Kloudgis.toolbarSearchController.searchChanged();
                    } else if (which === 27) {
                        //ESC pressed
                        this.invokeLater(this.clearField, 10);
                    }
                    return ret;
                },
                hint: "_search".loc(),

                clearField: function() {
                    this.set('value', '');
                }

            }),

            //infinite progress anim
            searchAnim: Kloudgis.ProgressLoopView.design({
                layout: {
                    width: 16,
                    height: 16,
                    centerY: 0,
                    centerX: 110
                },
                isVisible: NO,
                isVisibleBinding: 'Kloudgis.toolbarSearchController.progressVisible'
            }),

			logonUser: SC.ButtonView.design({
                layout: {
                    width: 120,
                    height: 24,
                    centerY: 0,
                    right: 10
                },
				classNames:'dark'.w(),
				//theme: 'capsule',
				toolTip: "_loginTooltip".loc(),
				titleBinding: 'Kloudgis.securityController.label',
                action: function() {
                    Kloudgis.securityController.showUserPicker(this);
                }
            })

        }),
        mapView: Kloudgis.OLMapView.design({
            layout: {
                top: 47,
                bottom: 0,
                left: 0,
                right: 0
            },
            backgroundColor: 'white',
        })

    }),

    //
    // Featuretypes and Feature Picker view + search
    //
    featurePickerView: Kloudgis.PickerPane.design({

        layout: {
            width: 265,
            height: 317
        },

        init: function() {
            sc_super();
            this.adjustSize();
        },

		closePicker: function(){
			if (Kloudgis.ftToolsController.get('activated')) {
                Kloudgis.ftToolsController.toggleActivated();
            }
			sc_super();		
		},

        adjustSize: function() {
            var base = Kloudgis.layerController.get('baseLayer');
            if (base === Kloudgis.layerController.GOOGLE) {
                this.adjust('height', 317);
            } else {
                this.adjust('height', 285);
            }
        }.observes('Kloudgis.layerController.baseLayer'),

        isModal: NO,
        acceptsKeyPane: NO,
        //acceptsKeyPane: NO, no key event trigger to this pane
        contentView: SC.View.design({
            childViews: 'controlBar listView baseLayerView googleTypeView'.w(),
            controlBar: SC.View.design({
                classNames: ['control-bar'],
                layout: {
                    left: 6,
                    right: 6,
                    top: 0,
                    height: 40
                },
                childViews: 'buttonLeft labelFt loadingImage buttonRight'.w(),
                //button to come back to featuretypes or to go to search	
                buttonLeft: SC.ButtonView.design({
                    classNames: 'dark back'.w(),
                    layout: {
                        width: 70,
                        height: 24,
                        centerY: 0,
                        left: 0
                    },
                    titleMinWidth: 0,
                    isVisibleBinding: 'Kloudgis.ftToolsController.btnLeftVisible',
                    title: "_layers".loc(),
                    target: 'Kloudgis.ftToolsController',
                    action: 'buttonLeftPressed'
                }),

                buttonRight: SC.ButtonView.design({
                    classNames: ['dark'],
                    layout: {
                        width: 70,
                        height: 24,
                        centerY: 0,
                        right: 0
                    },
                    titleMinWidth: 0,
                    buttonBehavior: SC.TOGGLE_BEHAVIOR,
                    isVisibleBinding: 'Kloudgis.ftToolsController.btnRightVisible',
                    valueBinding: 'Kloudgis.ftToolsController.btnRightValue',
                    title: "_edit".loc()
                }),
                //label to display "FeatureTypes" or the selected featuretype Ex: "Lot Occupe"
                labelFt: SC.LabelView.design(SC.Animatable, {
                    transitions: {
                        left: {
                            duration: .5,
                            timing: SC.Animatable.TRANSITION_CSS_EASE_IN
                        },
                    },
                    layout: {
                        centerY: 3,
                        right: 70,
                        left: 0,
                        height: 24
                    },
                    textAlign: null,
                    fontWeight: null,
                    classNames: 'tools-view-labels label-centered'.w(),
                    valueBinding: "Kloudgis.ftToolsController.activeLabel",
                    isVisibleBinding: 'Kloudgis.ftToolsController.labelVisible',

                    sceneDidChanged: function() {
                        var activeScene = Kloudgis.ftToolsController.get('activeScene');
                        if (activeScene === Kloudgis.ftToolsController.FT_VIEW) {
                            this.adjust('left', 0);
                            this.adjust('right', 70);
                        } else if (activeScene === Kloudgis.ftToolsController.FEATURE_VIEW) {
                            this.adjust('left', 70);
                            this.adjust('right', 0);
                        }
                    }.observes("Kloudgis.ftToolsController.activeScene")
                }),
                //infinite progress anim
                loadingImage: Kloudgis.ProgressLoopView.design(SC.Animatable, {
                    transitions: {
                        right: {
                            duration: .5,
                            timing: SC.Animatable.TRANSITION_CSS_EASE_IN
                        },
                    },
                    layout: {
                        width: 16,
                        height: 16,
                        centerY: 0,
                        right: 72
                    },
                    isVisibleBinding: 'Kloudgis.ftToolsController.progressVisible',

                    sceneDidChanged: function() {
                        var activeScene = Kloudgis.ftToolsController.get('activeScene');
                        if (activeScene === Kloudgis.ftToolsController.FT_VIEW) {
                            this.adjust('right', 72);
                        } else if (activeScene === Kloudgis.ftToolsController.FEATURE_VIEW) {
                            this.adjust('right', 2);
                        }
                    }.observes("Kloudgis.ftToolsController.activeScene")
                }),
            }),

            listView: SC.ContainerView.design({
                layout: {
                    height: 200,
                    right: 8,
                    left: 8,
                    top: 45
                },
				classNames: 'round-edges-for-list'.w(),	
                childViews: ['ftView', 'featureView'],
                contentView: SC.SceneView.design({
					layout:{
						top:5,
						bottom:5
					},
                    scenes: ['ftView', 'featureView'],
                    nowShowingBinding: 'Kloudgis.ftToolsController.activeScene',
                    transitionDuration: 400
                }),
            }),

            baseLayerView: SC.SegmentedView.design({
                layout: {
                    top: 250,
                    centerX: 0,
                    width: 270,
                    height: 24
                },

                valueBinding: 'Kloudgis.layerController.baseLayer',
                items: [{
                    title: '_google',
                    value: Kloudgis.layerController.GOOGLE,
                    tip: "_google_tooltip".loc()
                },
                {
                    title: '_osm',
                    value: Kloudgis.layerController.OSM,
                    tip: "_osm_tooltip".loc()
                }],
                itemTitleKey: 'title',
                itemValueKey: 'value',
                itemToolTipKey: 'tip'
            }),

            googleTypeView: SC.SegmentedView.design({
                layout: {
                    bottom: 10,
                    centerX: 0,
                    width: 270,
                    height: 24
                },
                valueBinding: 'Kloudgis.layerController.googleMapType',
                items: [{
                    title: '_road'.loc(),
                    value: google.maps.MapTypeId.ROADMAP,
                },
                {
                    title: '_satellite'.loc(),
                    value: google.maps.MapTypeId.SATELLITE,
                },
                {
                    title: '_terrain'.loc(),
                    value: google.maps.MapTypeId.TERRAIN,
                },
                {
                    title: 'hybrid'.loc(),
                    value: google.maps.MapTypeId.HYBRID,
                }],
                itemTitleKey: 'title',
                itemValueKey: 'value',

                googleBecomeActive: function() {
                    var active = Kloudgis.layerController.get('googleActive');
                    if (active) {
                        this.set('isVisible', YES);
                    } else {
                        this.set('isVisible', NO);
                    }
                }.observes('Kloudgis.layerController.googleActive')
            })
        }),

        showPicker: function() {
            var button = Kloudgis.activeRoute.currentPagePane.getPath('topToolbar.buttonTools');
			if(button){
            //console.log('Button: ' + button);
            	this.popup(button, SC.PICKER_POINTER, [3, 0, 1, 2, 2]);
			}
        },
    }),

    // layers in the picker view
    ftView: Kloudgis.LayerView.design({
        controller: 'Kloudgis.featuretypeController'
    }),
    //features on the selected layer
    featureView: Kloudgis.FeatureView.design({
        controller: 'Kloudgis.featureController'
    }),

    //
    // Lucene search result (from the main toolbar)
    //
    searchPickerView: Kloudgis.PickerPane.design({
        layout: {
            width: 250,
            height: 180
        },
        isModal: NO,
        acceptsKeyPane: NO,

		closePicker: function(){
			Kloudgis.toolbarSearchController.closePicker();
		},

        pickerShowingChanged: function() {
            var show = Kloudgis.toolbarSearchController.get('pickerShowing');
            if (!show) {
                this.remove();
            }
        }.observes("Kloudgis.toolbarSearchController.pickerShowing"),

        contentView: SC.View.design({
            childViews: 'controlBar listView'.w(),
            classNames: 'picker-main-view'.w(),
            controlBar: SC.View.design({
                classNames: 'picker-control-bar'.w(),
                childViews: ['remove', 'labelView', 'buttonLeft', 'loadingImage'],
                layout: {
                    left: 6,
                    right: 0,
                    top: 0,
                    height: 40
                },

                labelView: SC.LabelView.design(SC.Animatable, {
                    transitions: {
                        left: {
                            duration: .1,
                            timing: SC.Animatable.TRANSITION_CSS_EASE
                        },
                        right: {
                            duration: .1,
                            timing: SC.Animatable.TRANSITION_CSS_EASE
                        },
                    },
                    layout: {
                        centerY: 3,
                        right: 0,
                        left: 0,
                        height: 24
                    },
                    textAlign: null,
                    fontWeight: null,
                    classNames: 'tools-view-labels label-centered'.w(),
                    valueBinding: "Kloudgis.toolbarSearchController.activeLabel",
                    isVisibleBinding: 'Kloudgis.toolbarSearchController.labelVisible',

                    sceneDidChanged: function() {
                        var activeScene = Kloudgis.toolbarSearchController.get('activeScene');
                        if (activeScene === Kloudgis.toolbarSearchController.FT_VIEW) {
                            this.adjust('left', 0);
                            this.adjust('right', 0);
                        } else if (activeScene === Kloudgis.toolbarSearchController.FEATURE_VIEW) {
                            this.adjust('left', 38);
                            this.adjust('right', 0);
                        }
                    }.observes("Kloudgis.toolbarSearchController.activeScene")
                }),

                buttonLeft: SC.ButtonView.design({
                    classNames: 'dark back'.w(),
                    layout: {
                        width: 70,
                        height: 24,
                        centerY: 0,
                        left: 0
                    },
                    titleMinWidth: 0,
                    isVisibleBinding: 'Kloudgis.toolbarSearchController.btnLeftVisible',
                    title: "_results".loc(),
                    target: 'Kloudgis.toolbarSearchController',
                    action: 'buttonLeftPressed'
                }),

                //infinite progress anim
                loadingImage: Kloudgis.ProgressLoopView.design({
                    layout: {
                        width: 16,
                        height: 16,
                        centerY: 0,
                        right: 3
                    },					
					init: function(){
						sc_super();
						this.bind('isVisible', SC.Binding.from('Kloudgis.toolbarSearchController.progressVisible').oneWay().bool());
						this.bind('isVisible', SC.Binding.from('Kloudgis.toolbarSearchFeatureController.progressVisible').oneWay().bool());
					}
                }),

                remove: Kloudgis.CloseDialogButtonView.design({
                    layout: {
                        right: -25,
                        top: -20,
                        width: 30,
                        height: 30
                    },
                    action: function() {
                        Kloudgis.toolbarSearchController.closePicker();
                    }
                }),
            }),

            listView: SC.ContainerView.design({
                layout: {
                    bottom: 11,
                    right: 8,
                    left: 8,
                    top: 42
                },
				classNames: 'round-edges-for-list'.w(),
                childViews: 'ftViewSearch featureViewSearch'.w(),
                contentView: SC.SceneView.design({
					layout:{
						top:5,
						bottom:5
					},
                    scenes: ['ftViewSearch', 'featureViewSearch'],
                    nowShowingBinding: 'Kloudgis.toolbarSearchController.activeScene',
                    transitionDuration: 400
                }),
            })
        }),

        showPicker: function() {
			var text = Kloudgis.activeRoute.currentPagePane.getPath('topToolbar.toolbarSearchText');
            this.popup(text, SC.PICKER_POINTER, [3, 0, 1, 2, 2]);
        }
    }),

    ftViewSearch: Kloudgis.FeaturetypeView.design({
        controller: 'Kloudgis.toolbarSearchController'
    }),
    featureViewSearch: Kloudgis.FeatureView.design({
        controller: 'Kloudgis.toolbarSearchFeatureController'
    }),

});
