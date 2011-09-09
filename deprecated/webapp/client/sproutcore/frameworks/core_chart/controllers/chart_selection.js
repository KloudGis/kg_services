// ==========================================================================
// Project:   CoreChart.transactionController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
CoreChart.chartSelectionController = SC.ArrayController.create(
/** @scope CoreChart.featuresController.prototype */
{

    allowsMultipleSelection: NO,
    query: null,
    progressVisible: NO,
    activeLabel: '',
    iProgressCount: 0,
    iQueryCount: 0,
    _picker: null,
	posTimer: null,

    showResult: function(query, anchor) {
        this.closePicker();
        if (query) {
            query.fetchCallbackReference = this;
            query.fetchStart = this.fetchQueryStart;
            query.fetchEnd = this.fetchQueryEnd;
        }
        this.set('query', query);
        this.set('activeLabel', query.label);
        this.showPicker(anchor);
        this.refreshList();
    },

    inspectorDidMove: function() {
        if (!SC.none(this._picker)) {
            if (SC.none(Kloudgis.inspectorController.get('lastInspectorLayout'))) {
                this.closePicker();
            } else {
                if (this.posTimer) {
                    this.posTimer.invalidate();
                }
                this.posTimer = SC.Timer.schedule({
                    target: this,
                    action: 'reculatePosition',
                    interval: 300,
                    repeats: NO
                });
            }
        }
    }.observes('Kloudgis.inspectorController.lastInspectorLayout'),

    reculatePosition: function() {
        if (this._picker) {
            this._picker.positionPane(NO);
        }
    },

    activateProgress: function(vis) {
        this.set('progressVisible', vis);
    },

    refreshList: function() {
        if (!SC.none(this.query)) {
            this.activateProgress(YES);
            this.set('iQueryCount', this.get('iQueryCount') + 1);
            this.set('iProgressCount', 0);
            this.query.set('queryId', this.get('iQueryCount'));
            var res = Kloudgis.store.find(this.query);
            this.set('content', res);
        } else {
            this.set('content', null);
        }
    },

    fetchQueryStart: function(query) {
        if (query.get('queryId') === this.get('iQueryCount')) {
            this.set('iProgressCount', this.get('iProgressCount') + 1);
            this.activateProgress(YES);
        }
    },

    fetchQueryEnd: function(query) {
        if (query.get('queryId') === this.get('iQueryCount')) {
            var count = this.get('iProgressCount');
            if (count > 0) {
                this.set('iProgressCount', count - 1);
            }
            if (count <= 1) {
                this.activateProgress(NO);
            }
        }
    },

    selectionDidChanged: function() {
        console.log('zooom');
        this.invokeLater('_zoomSelection', 200);
    }.observes('selection'),

    _zoomSelection: function() {
        var _sel = this.get('selection');
        if (!SC.none(_sel)) {
            var _fea = _sel.get('firstObject');
            if (_fea) {
                Kloudgis.mapController.zoomHighlightFeature(_fea);
            }
        }
    },

    showPicker: function(anchor) {
        if (!this._picker) {
            this.buildPicker();
        }
        this._picker.popup(anchor, SC.PICKER_POINTER, [3, 0, 1, 2, 2]);
        this.set('pickerVisible', YES);
    },

    closePicker: function() {
        if (!SC.none(this._picker)) {
            this._picker.remove();
        }
        this._picker = null;
        this.set('pickerVisible', NO);
        this.set('content', null);
        this.set('activeLabel', '');
        this.set('progressVisible', NO);
        this.set('query', null);
    },

    buildPicker: function() {
        var newpicker = Kloudgis.PickerPane.create({
            layout: {
                width: 250,
                height: 200
            },
            isModal: NO,
            acceptsKeyPane: NO,

            contentView: SC.View.design({
                classNames: 'picker-main-view'.w(),
                childViews: 'controlBar listView'.w(),
                controlBar: SC.View.design({
                    classNames: 'picker-control-bar'.w(),
                    childViews: ['remove', 'labelView', 'loadingImage'],
                    layout: {
                        left: 6,
                        right: 0,
                        top: 0,
                        height: 40
                    },
                    labelView: SC.LabelView.design({
                        layout: {
                            centerY: 3,
                            right: 20,
                            left: 20,
                            height: 24
                        },
                        textAlign: null,
                        fontWeight: null,
                        classNames: 'tools-view-labels label-centered'.w(),
                        valueBinding: "CoreChart.chartSelectionController.activeLabel",
                    }),

                    //infinite progress anim
                    loadingImage: Kloudgis.ProgressLoopView.design({
                        layout: {
                            width: 16,
                            height: 16,
                            centerY: 0,
                            right: 5
                        },
                        isVisibleBinding: 'CoreChart.chartSelectionController.progressVisible'
                    }),

                    remove: Kloudgis.CloseDialogButtonView.design({
                        layout: {
                            right: -25,
                            top: -20,
                            width: 30,
                            height: 30
                        },
                        action: function() {
                            CoreChart.chartSelectionController.closePicker();
                        }
                    })
                }),

                listView: SC.ContainerView.design({
                    layout: {
                        bottom: 11,
                        right: 8,
                        left: 8,
                        top: 45
                    },
                    classNames: 'round-edges-for-list'.w(),
                    childViews: 'ftView featureView'.w(),
                    contentView: Kloudgis.FeatureView.design({
                        layout: {
                            top: 5,
                            bottom: 5
                        },
                        controller: 'CoreChart.chartSelectionController'
                    }),
                })
            })
        });
        this._picker = newpicker;
    }
});
