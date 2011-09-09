// ==========================================================================
// Project:   Kloudgis.mapControlController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Controller for the map control : selection + draw tools

  @extends SC.Object
*/
sc_require('models/featuretype')
Kloudgis.mapControlController = SC.ObjectController.create(
/** @scope Kloudgis.mapControlController.prototype */
{
    selectionControl: null,
    editSelectionControl: null,
    drawPointControl: null,
    drawLineControl: null,
    drawPolygonControl: null,
    editControl: null,

    controls: [],

    drawLayer: null,

    //name of the map tool activated - select, edit or create
    activeTool: null,
    //point, line or polygon
    createType: 'point',
    //edit tool handler
    editToolHandler: null,
    //wms layer to select in.  Order is important
    wmsLayersSelection: null,

    selectionCounter: 0,

    activeToolChanged: function() {
        var active = this.get('activeTool');
        if (active === 'edit') {
            this.activateEdit();
        } else if (active === 'create') {
            this.activateCreate();
        } else {
            this.activateSelection();
        }
    }.observes('activeTool'),

    activateSelection: function() {
        console.log('selection about to be activated');
        var control = this.get('selectionControl');
        if (control) {
            this.deactivateControlExcept('selectionControl');
            control.activate();
            return YES;
        }
        return NO;
    },

    activateEdit: function() {
        var control = this.get('editControl');
        if (control) {
            this.deactivateControlExcept('editControl');
            this.editSelectionControl.activate();
            control.activate();
            if (this.get('editToolHandler') && this.get('editToolHandler').get('editHandlerGetActiveFeature')) {
                var active = this.get('editToolHandler').editHandlerGetActiveFeature();
                if (!SC.none(active)) {
                    var ft = active.get('featuretype');
                    if (!SC.none(ft)) {
                        Kloudgis.layerController.getFeatureOLFromFeaturetype(active.get('id'), ft, this, this.beginEditFeature);
                    }
                }
            }
            return YES;
        }
        return NO;
    },

    beginEditFeature: function(request) {
        this.featureEditSelected({
            features: request.features
        });
    },

    activateCreate: function() {
        if (this.get('createType') === 'line') {
            this.activateLineDraw();
        } else if (this.get('createType') === 'polygon') {
            this.activatePolygonDraw();
        } else {
            this.activatePointDraw();
        }
    },

    createTypeChanged: function() {
        if (this.get('activeTool') === 'create') {
            this.activateCreate();
        }
    }.observes('createType'),

    activatePointDraw: function() {
        var control = this.get('drawPointControl');
        if (control) {
            this.deactivateControlExcept('drawPointControl');
            control.activate();
            return YES;
        }
        return NO;
    },

    activateLineDraw: function() {
        var control = this.get('drawLineControl');
        if (control) {
            this.deactivateControlExcept('drawLineControl');
            control.activate();
            return YES;
        }
        return NO;
    },

    activatePolygonDraw: function() {
        var control = this.get('drawPolygonControl');
        if (control) {
            this.deactivateControlExcept('drawPolygonControl');
            control.activate();
            return YES;
        }
        return NO;
    },

    deactivateControlExcept: function(controlName) {
        var control = this.get(controlName);
        var len = this.controls.length;
        var i = 0;
        for (i = 0; i < len; i++) {
            var controlWrap = this.controls[i];
            if (controlWrap.control !== control) {
                controlWrap.control.deactivate();
            }
        }
    },

    addWMSSelectionControl: function() {
        var sControl = this.createSelectionControl();
        var mapview = Kloudgis.mapController.getMap();
        mapview.addControl(sControl);
        this.set('selectionControl', sControl);
        //selection listener
        sControl.events.register("getfeatureinfo", this, this.featureSelected);
        this.controls.push({
            control: sControl
        });
    },

    createSelectionControl: function() {
        return new OpenLayers.Control.WMSGetFeatureInfo({
            queryVisible: true,
            infoFormat: 'application/vnd.ogc.gml',
            url: '/geoserver/wms',
            layerUrls: ["/geoserver/gwc/service/wms"],
            findLayers: function() {
                if (Kloudgis.mapControlController.wmsLayersSelection) {
                    var len = Kloudgis.mapControlController.wmsLayersSelection.length;
                    var visibleLayers = [];
                    var i;
                    for (i = 0; i < len; i++) {
                        var layer = Kloudgis.mapControlController.wmsLayersSelection[i];
                        if (layer.getVisibility()) {
                            visibleLayers.push(layer);
                        }
                    }
                    return visibleLayers;
                } else {
                    //bug fix in OL.  Reverse the layers order to select the top layers first (not from the bottom)!
                    //call the super
                    var layers = OpenLayers.Control.WMSGetFeatureInfo.prototype.findLayers.apply(this);
                    var reversed = [];
                    var len = layers.length;
                    for (i = len - 1; i >= 0; i--) {
                        reversed.push(layers[i]);
                    }
                    return reversed;
                }
            }
        });
    },

    addDrawFeatureControls: function() {
        var style = new OpenLayers.Style({
            'externalGraphic': sc_static('images/map/edit_ball.png'),
            'graphicHeight': 16,
            'graphicWidth': 16
        });
        var styleMap = new OpenLayers.StyleMap({
            'default': style
        });

        var drawLayer = new OpenLayers.Layer.Vector("Draw", {
            isBaseLayer: false,
            displayInLayerSwitcher: false,
            styleMap: styleMap
        });
        this.set('drawLayer', drawLayer);
        Kloudgis.mapController.addLayer(drawLayer);
        var point = new OpenLayers.Control.DrawFeature(drawLayer, OpenLayers.Handler.Point);
        var line = new OpenLayers.Control.DrawFeature(drawLayer, OpenLayers.Handler.Path);
        var polygon = new OpenLayers.Control.DrawFeature(drawLayer, OpenLayers.Handler.Polygon);
        var edit = new OpenLayers.Control.ModifyFeature(drawLayer);
        edit.mode = OpenLayers.Control.ModifyFeature.DRAG | OpenLayers.Control.ModifyFeature.RESHAPE;
        this.set('drawPointControl', point);
        this.set('drawLineControl', line);
        this.set('drawPolygonControl', polygon);
        this.set('editControl', edit);
        var mapview = Kloudgis.mapController.getMap();
        mapview.addControl(point);
        mapview.addControl(line);
        mapview.addControl(polygon);
        mapview.addControl(edit);
        point.events.register("featureadded", this, this.geometryCreated);
        line.events.register("featureadded", this, this.geometryCreated);
        polygon.events.register("featureadded", this, this.geometryCreated);
        edit.layer.events.register("afterfeaturemodified", this, this.geometryEdited);

        this.controls.push({
            control: point
        });
        this.controls.push({
            control: line
        });
        this.controls.push({
            control: polygon
        });
        this.controls.push({
            control: edit
        });

        //edit selection control
        var sControl = this.createSelectionControl();
        var mapview = Kloudgis.mapController.getMap();
        mapview.addControl(sControl);
        this.set('editSelectionControl', sControl);
        //selection listener
        sControl.events.register("getfeatureinfo", this, this.featureEditSelected);
        this.controls.push({
            control: sControl
        });
    },

    featureEditSelected: function(e) {
        var features = e.features;
        if (this.editSelectionControl.active && features.length > 0) {
            var feature = features[0];
            var editable = NO;
            if (this.get('editToolHandler') && this.get('editToolHandler').get('editHandlerEditable')) {
                editable = this.get('editToolHandler').editHandlerEditable(feature);
            }
            if (editable) {
                var mapController = Kloudgis.mapController;
                var geom = feature.geometry.transform(mapController.getLonLatProj(), mapController.getMap().getProjectionObject());
                this.drawLayer.addFeatures(feature);
                this.get('editControl').selectControl.select(feature);
            }
        }
    },

    featureSelected: function(e) {
        var features = e.features;
        if (features.length > 0) {
            this.incrementProperty('selectionCount');
            var feature = features[0];
            var firstFeaturetype = feature.gml.featureType;
            var query = SC.Query.create({
                recordType: Kloudgis.Featuretype,
                conditions: "table_name = '%@'".fmt(firstFeaturetype),
            });
            // featuretypes
            var fts = Kloudgis.store.find(Kloudgis.FEATURETYPE_LIST);
            //filter the list to match table_name
            var result = fts.find(query);
            if (result && result.get('length') > 0) {
                var ft = result.objectAt(0);
                //get the name of the attribute used to sort the priority
                var priorityAttr = ft.get('prioritySelectionAttr');
                var priorityItems;
                if (!SC.empty(priorityAttr)) {
                    var at = ft.getAttrType(priorityAttr);
                    if (!SC.none(at)) {
                        var manyArray = at.get('selectionPriority');
                        if (!SC.none(manyArray)) {
                            //priority spec to this attribute
                            priorityItems = manyArray.toArray();
                        }
                    }
                }
                var goodFeatures = [];
                var ids = [];
                var i;
                //extract the feature with the same featuretype (like the first one)
                for (i = 0; i < features.length; i++) {
                    var feaM = features[i];
                    if (feaM.gml.featureType === firstFeaturetype) {
                        var id = this.extractId(feaM);
                        ids.push(id);
                        goodFeatures.push(feaM);
                    }
                }
                if (ids.length === 1) {
                    //one feature selected
                    this.showSelection(ft, ids[0], null, e.xy, feature);
                } else {
                    if (SC.none(priorityItems) || priorityItems.get('length') === 0) {
                        //no selection priority set
                        var first = ids[0];
                        var rest = ids.splice(1, ids.length - 1);
                        this.showSelection(ft, first, rest, e.xy, feature);
                    } else {
                        //filter using the priority items
                        this.showSelectionPriority(ft, priorityItems, goodFeatures, e.xy);
                    }
                }
            }
        }
    },

    //extract the FID from a open_layer feature
    extractId: function(ol_feature) {
        var id = ol_feature.fid.split('.')[1];
        return id;
    },

    //filter by priority
    showSelectionPriority: function(ft, priorityItems, features, xy) {
        var len = priorityItems.get('length');
        var ready = YES;
        for (i = 0; i < len; i++) {
            var prio = priorityItems.objectAt(i);
            if (prio.get('status') & SC.Record.BUSY) {
                ready = NO;
                //wait until the loading is done
                prio.addObserver('status', this, this.priorityReady, {
                    counter: this.selectionCounter,
                    ft: ft,
                    priority_items: priorityItems,
                    features: features,
                    xy: xy
                });
            }
        }
        if (ready) {
            //no need to wait, the priority items are already available
            this.doShowSelectionPriority(ft, priorityItems, features, xy);
        }
    },

    //call back when the status change on the priority Record
    priorityReady: function(priority, key, nothing, context, rev) {
        console.log('priority from observer.');
        //if its still the current selection (relevent)
        if (context.counter === this.selectionCounter) {
            var priorityItems = context.priority_items;
            var len = priorityItems.get('length');
            var ready = YES;
            for (i = 0; i < len; i++) {
                var prio = priorityItems.objectAt(i);
                if (prio.get('status') & SC.Record.BUSY) {
                    ready = NO;
                }
            }
            if (ready) {
                this.doShowSelectionPriority(context.ft, context.priority_items, context.features, context.xy);
            }
        }
        priority.removeObserver('status', this, this.priorityReady, context);
    },

    //perform the order by
    doShowSelectionPriority: function(ft, priorityItems, features, xy) {
        var sorted = SC.Set.create();
        var len = priorityItems.get('length');
        var lenF = features.length;
        var i, j;
        var attrName;
        for (i = 0; i < len; i++) {
            var prio = priorityItems[i];
            var prioValue = prio.get('attr_value');
            if (!attrName) {
                var attr = prio.get('attribute');
                if (attr) {
                    attrName = attr.get('name');
                }
            }
            if (attrName) {
                for (j = 0; j < lenF; j++) {
                    var item = features[j].data[attrName];
                    //if value from ol feature matches the priority item value => add it to the sorted list
                    if (item === prioValue) {
                        sorted.add(features[j]);
                    }
                }
            }
        }
        //add the features that do not match a priority item
        if (sorted.length < features.length) {
            var len = features.length;
            var i;
            for (i = 0; i < len; i++) {
                if (!sorted.contains(features[i])) {
                    sorted.add(features[i]);
                }
            }
        }
        var otherIds = [];
        len = sorted.length;
        var sortedArray = sorted.toArray();
        for (i = 1; i < len; i++) {
            otherIds.push(this.extractId(sortedArray[i]));
        }
        //show the selection with the new first feature and with others in the wanted order
        var first = sortedArray[0];
        this.showSelection(ft, this.extractId(first), otherIds, xy, first);
    },

    showSelection: function(ft, id, othersId, xy, feature) {
        var kfeature;
        var rType = ft.get('recordType');
        if (rType) {
            kfeature = Kloudgis.store.find(rType, id);
        }
        if (kfeature) {
            var mapController = Kloudgis.mapController;
            var geom, fBounds;
            if (feature && feature.geometry) {
                geom = feature.geometry.transform(mapController.getLonLatProj(), mapController.getMap().getProjectionObject());
                fBounds = geom.getBounds();
            }
            //already selection made
            if (kfeature.get('featureController').get('length') > 0) {
				//add to selection without info balloon
				Kloudgis.infoController.setValues(xy, fBounds, kfeature, othersId);
				Kloudgis.infoController.selectFeature();
			} else {
                Kloudgis.infoController.showPicker(xy, fBounds, kfeature, othersId);
                mapController.clearTempHighlight();
                if (!SC.none(geom)) {
                    mapController.tempHighlightFeatureGeometry(geom);
                } else {
                    //fetch from our server
                    if (kfeature.get('status') & SC.Record.BUSY) {
                        kfeature.addObserver('status', this, this.featureReady, {
                            counter: this.selectionCounter
                        });
                    } else {
                        mapController.tempHighlightFeature(kfeature);
                    }
                }
            }
        }
    },

    featureReady: function(feature, key, nothing, context, rev) {
        if (context.counter === this.selectionCounter) {
            Kloudgis.mapController.highlightSelectionFeature(feature);
        }
    },

    geometryCreated: function(e) {
        if (this.get('editToolHandler') && this.get('editToolHandler').get('editHandlerCreation')) {
            var geoLonLat = e.feature.geometry.transform(Kloudgis.mapController.getMap().getProjectionObject(), Kloudgis.mapController.getLonLatProj());
            this.get('editToolHandler').editHandlerCreation(geoLonLat.toString(), e.feature);
        }
        this.cleanLayer();
        this.deactivateControlExcept();
        this.set('activeTool', 'selection');
    },

    geometryEdited: function(e) {
        console.log("edit finished");
        this.cleanLayer();
        if (this.get('activeTool') === 'edit') {
            this.deactivateControlExcept();
            this.editSelectionControl.deactivate();
            this.set('activeTool', 'selection');
        }
        if (this.get('editToolHandler') && this.get('editToolHandler').get('editHandlerEdition')) {
            var geoLonLat = e.feature.geometry.transform(Kloudgis.mapController.getMap().getProjectionObject(), Kloudgis.mapController.getLonLatProj());
            this.get('editToolHandler').editHandlerEdition(geoLonLat.toString(), e.feature);
        }
    },

    cleanLayer: function() {
        this.drawLayer.removeAllFeatures();
    }

});
