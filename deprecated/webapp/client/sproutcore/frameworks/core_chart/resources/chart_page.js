// ==========================================================================
// Project:   CoreChart - chartPage
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

// This page describes the main user interface for your application.
CoreChart.chartPage = SC.Page.extend({

    mainPane: SC.MainPane.design({
        childViews: 'topToolbar listChart'.w(),

        topToolbar: SC.ToolbarView.design({
            layout: {
                "top": 0,
                "left": 0,
                "right": 0,
                "height": 46
            },
            classNames: ['toolbar'],
            childViews: 'mapButton toolbarSearchText searchAnim removeAll'.w(),

            mapButton: SC.ButtonView.design({
                layout: {
                    top: 10,
                    height: 24,
                    left: 10,
                    width: 110,
                },
				classNames: 'dark'.w(),
                icon: sc_static('images/environment.png'),
				toolTip: "_goToMap".loc(),
				title: "_mapPage".loc(),
                action: function() {
					CoreChart.chartListController.removeAllChart();
                    SC.routes.set('location', 'main');
                }
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
                valueBinding: 'CoreChart.chartToolbarSearchController.search',

                keyDown: function(evt) {
                    var ret = sc_super();
                    var which = evt.which;
                    //console.log('key down search:' + which);
                    if (which === 13 && !evt.isIMEInput) {
                        //ENTER pressed
                        CoreChart.chartToolbarSearchController.searchChanged();
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
                isVisibleBinding: 'CoreChart.chartToolbarSearchController.progressVisible'
            }),

            removeAll: SC.ButtonView.design({
                layout: {
                    width: 120,
                    height: 24,
                    centerY: 0,
                    right: 10
                },
				classNames: 'dark'.w(),
                title: '_removeAll'.loc(),
                action: function() {
                    CoreChart.chartListController.removeAllChart();
                }
            })

        }),

        listChart: SC.ScrollView.design({
            layout: {
                top: 47,
                left: 0,
                bottom: 0,
                right: 0
            },

            contentView: SC.ListView.design({
                rowHeight: 250,
                contentValueKey: "feature",
                canDeleteContent: NO,
                hasContentIcon: NO,
                contentExampleViewKey: 'chartsView',
                contentBinding: 'CoreChart.chartListController.arrangedObjects',
                selectionBinding: 'CoreChart.chartListController.selection',

                init: function() {
                    sc_super();
                    CoreChart.chartListController.addObserver('length', this, this.lengthChanged);
                },

                lengthChanged: function() {
                    this.invokeLater(function() {
                        this.scrollToContentIndex(CoreChart.chartListController.get('length') - 1);
                    },
                    100);
                },

                contentIndexesInRect: function(rect) {
                    return null; //render all items.
                }
            })
        }),
    }),

    chartSearchPickerView: Kloudgis.PickerPane.design({
        layout: {
            width: 250,
            height: 175
        },
        isModal: NO,
        acceptsKeyPane: NO,

        closePicker: function() {
            CoreChart.chartToolbarSearchController.closePicker();
        },

        pickerShowingChanged: function() {
            var show = CoreChart.chartToolbarSearchController.get('pickerShowing');
            if (!show) {
                this.remove();
            }
        }.observes("CoreChart.chartToolbarSearchController.pickerShowing"),

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
                    valueBinding: "CoreChart.chartToolbarSearchController.activeLabel",
                    isVisibleBinding: 'CoreChart.chartToolbarSearchController.labelVisible',

                    sceneDidChanged: function() {
                        var activeScene = CoreChart.chartToolbarSearchController.get('activeScene');
                        if (activeScene === CoreChart.chartToolbarSearchController.FT_VIEW) {
                            this.adjust('left', 0);
                            this.adjust('right', 0);
                        } else if (activeScene === CoreChart.chartToolbarSearchController.FEATURE_VIEW) {
                            this.adjust('left', 38);
                            this.adjust('right', 0);
                        }
                    }.observes("CoreChart.chartToolbarSearchController.activeScene")
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
                    isVisibleBinding: 'CoreChart.chartToolbarSearchController.btnLeftVisible',
                    title: "_results".loc(),
                    target: 'CoreChart.chartToolbarSearchController',
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
						this.bind('isVisible', SC.Binding.from('CoreChart.chartToolbarSearchController.progressVisible').oneWay().bool());
						this.bind('isVisible', SC.Binding.from('CoreChart.chartToolbarSearchFeatureController.progressVisible').oneWay().bool());
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
                        CoreChart.chartToolbarSearchController.closePicker();
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
                childViews: 'ftViewSearch featureViewSearch'.w(),
				classNames: 'round-edges-for-list'.w(),	
                contentView: SC.SceneView.design({
					layout:{
						top:5,
						bottom:5
					},
                    scenes: ['ftViewSearch', 'featureViewSearch'],
                    nowShowingBinding: 'CoreChart.chartToolbarSearchController.activeScene',
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
        controller: 'CoreChart.chartToolbarSearchController'
    }),
    featureViewSearch: Kloudgis.SimpleFeatureView.design({
        controller: 'CoreChart.chartToolbarSearchFeatureController',
    }),
})
