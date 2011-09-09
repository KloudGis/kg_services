// ==========================================================================
// Project:   Kloudgis.infoController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
sc_require('views/picker_pane')
Kloudgis.infoController = SC.ObjectController.create(
/** @scope Kloudgis.infoController.prototype */
{
    //picker info on the map
    infoPicker: null,
    _bounds: null,
    pixel: null,
    more_selection_ids: null,

    setValues: function(pixel, bounds, feature, more_selection_ids) {
        this.set('pixel', pixel);
        this._bounds = bounds;
        this.set('more_selection_ids', more_selection_ids);
        this.removeObserver();
        //cleanup		
        this.set('content', feature);
    },

    showPicker: function(pixel, bounds, feature, more_selection_ids) {
        this.setValues(pixel, bounds, feature, more_selection_ids);
        this._showPicker(feature);
    },

    _showPicker: function(feature) {
        if (!this.infoPicker) {
            this.buildPicker();
        }
        if (SC.none(feature) || !feature.get('featuretype').get('selectionable')) {
            this.infoPicker.contentView.buttonSelect.set('isVisible', NO);
        } else {
            this.infoPicker.contentView.buttonSelect.set('isVisible', YES);
        }
        if (feature.get('status') === SC.Record.BUSY_LOADING) {
            this.infoPicker.contentView.labelMain.set('value', 'Chargement...');
            this.infoPicker.contentView.labelSub.set('value', '');
            feature.addObserver('status', this, this.featureStatusObserver);
        } else {
            this.infoPicker.contentView.labelMain.set('value', feature.get('label'));
            if (this.get('more_selection_ids') && this.get('more_selection_ids').length > 0) {
                this.infoPicker.contentView.labelSub.set('value', '_features_selected'.loc(this.get('more_selection_ids').length));
            } else {
                this.infoPicker.contentView.labelSub.set('value', feature.get('labelInfo'));
            }
            if (SC.none(this._bounds)) {
                this.setBoundsFromFeature(feature);
            }
        }
        this.infoPicker.popup(null, SC.PICKER_POINTER, [2, 3, 0, 1, 2]);
        this.infoPicker.set('anchorCached', Kloudgis.infoController.computeAnchor());
        this.infoPicker.positionPane(YES);
        //trick to force repaint (and make it nicer in the mean time!)
        this.infoPicker.adjust('top', this.infoPicker.get('layout').top + 1);
        this.infoPicker.adjust('opacity', 1.0);

    },

    featureStatusObserver: function() {
        var feature = this.get('content');
        if (feature && feature.get('status') !== SC.Record.BUSY_LOADING) {
            this.infoPicker.contentView.labelMain.set('value', feature.get('label'));
            if (this.get('more_selection_ids') && this.get('more_selection_ids').length > 0) {
                this.infoPicker.contentView.labelSub.set('value', '_features_selected'.loc(this.get('more_selection_ids').length));
            } else {
                this.infoPicker.contentView.labelSub.set('value', feature.get('labelInfo'));
            }
            if (SC.none(this._bounds)) {
                this.setBoundsFromFeature(feature);
            }
        }
    },

    setBoundsFromFeature: function(feature) {
        var boundsLonLat = feature.get('boundsLonLat');
        if (!SC.none(boundsLonLat)) {
            var boundsMap = boundsLonLat.transform(Kloudgis.mapController.getLonLatProj(), Kloudgis.mapController.getMap().getProjectionObject());
            this._bounds = boundsMap;
        }
    },

    computeAnchor: function(forceBounds) {
        var offsetMap = SC.viewportOffset(Kloudgis.mapController.get('content').get('layer'));
        if (forceBounds && !SC.none(this._bounds)) {
            var bounds = this._bounds;
            var lonlatMin = new OpenLayers.LonLat(bounds.left, bounds.top);
            var lonlatMax = new OpenLayers.LonLat(bounds.right, bounds.bottom);
            var pixelMin = Kloudgis.mapController.getMap().getPixelFromLonLat(lonlatMin);
            var pixelMax = Kloudgis.mapController.getMap().getPixelFromLonLat(lonlatMax);
            var x = pixelMin.x + offsetMap.x;
            var y = pixelMin.y + offsetMap.y;
            var w = pixelMax.x - pixelMin.x;
            var h = pixelMax.y - pixelMin.y;
            var rect = {
                x: x + Math.floor(w / 4),
                //add half the picker height
                y: y + Math.floor(h / 4),
                width: Math.floor(w / 2),
                height: Math.floor(h / 2)
            }
            return rect;
        } else {
            var x = this.pixel.x + offsetMap.x;
            var y = this.pixel.y + offsetMap.y;
            var rect = {
                x: x,
                y: y,
                width: 1,
                height: 1
            }
            return rect;
        }
    },

    selectFeature: function() {
        var selection = [this.get('content')];
        var othersIds = this.get('more_selection_ids');
        if (othersIds && othersIds.length > 0) {
            var ft = selection[0].get('featuretype');
            if (!SC.none(ft)) {
                var rectT = ft.get('recordType');
                if (!SC.none(rectT)) {
                    var i;
                    for (i = 0; i < othersIds.length; i++) {
                        var fea = Kloudgis.store.find(rectT, othersIds[i]);
                        if (!SC.none(fea)) {
                            selection.push(fea);
                        }
                    }
                }
            }
        }
        Kloudgis.inspectorController.featuresSelected(selection);
        if (!SC.none(this.infoPicker)) {
            this.infoPicker.remove();
            this.infoPicker.adjust('opacity', 0.0);
        }
    },

    closePicker: function() {
        this.infoPicker.remove();
        this.infoPicker.adjust('opacity', 0.0);
        this.removeObserver();
        this.set('content', null);
    },

    removeObserver: function() {
        if (!SC.none(this.get('content'))) {
            this.get('content').removeObserver('status', this, this.featureStatusObserver);
        }
    },

    buildPicker: function() {
        this.infoPicker = Kloudgis.PickerPane.create(SC.Animatable, {
            transitions: {
                top: {
                    duration: .5,
                    timing: SC.Animatable.TRANSITION_CSS_EASE
                },
                opacity: {
                    duration: .5,
                    timing: SC.Animatable.TRANSITION_CSS_EASE_IN_OUT,
                },
            },
            layout: {
                width: 175,
                height: 40,
            },
            pointerOffset: [18, -18, -18, 18],
            extraRightOffset: 100,
            pointerPos: 'perfectTop',
            contentView: SC.View.design({
                layout: {
                    top: 3,
                    left: 10,
                    right: 1,
                    bottom: 3
                },
                childViews: 'labelMain labelSub buttonSelect'.w(),
                buttonSelect: Kloudgis.ToolbarButtonView.design({
                    layout: {
                        right: 0,
                        centerY: 0,
                        width: 30,
                        height: 30
                    },
                    hasIcon: YES,
                    titleMinWidth: 0,
                    //icon: "sc-icon-help-24",
                    controlSize: SC.LARGE_CONTROL_SIZE,
                    classNames: 'info-disclose-button'.w(),
                    icon: sc_static('images/buttons/right_arrow_32.png'),
                    action: function() {
                        Kloudgis.infoController.selectFeature();
                    }
                }),
                labelMain: SC.LabelView.design({
                    layout: {
                        top: 0,
                        left: 0,
                        right: 31,
                        height: 18
                    },
                    classNames: ['info-main-label'],
                }),
                labelSub: SC.LabelView.design({
                    layout: {
                        bottom: 0,
                        left: 0,
                        right: 31,
                        height: 18
                    },
                    classNames: ['info-sub-label'],
                })
            }),

            windowSizeDidChange: function() {
                //	sc_super();
                this.invokeLater('computeAnchorBounds', 100);
            },

            computeAnchorBounds: function() {
                this.set('anchorCached', Kloudgis.infoController.computeAnchor(YES));
                this.positionPane(YES);
            },

            modalPaneDidClick: function(evt) {
                var f = this.get("frame");
                if (!this.clickInside(f, evt)) {
                    Kloudgis.infoController.closePicker();
                    Kloudgis.mapController.clearTempHighlight();
                }
                return YES;
            }

        });
        this.infoPicker.adjust('opacity', 0.0);
    }

});
