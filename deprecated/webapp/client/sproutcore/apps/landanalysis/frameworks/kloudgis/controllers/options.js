// ==========================================================================
// Project:   Kloudgis.optionsController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
sc_require('views/menu_list_item')
Kloudgis.optionsController = SC.ArrayController.create(
/** @scope Kloudgis.optionsController.prototype */
 {
    allowsMultipleSelection: NO,
	activated: NO,
    _picker: null,

    showPicker: function(anchor) {
		this.set('activated', YES);
        var createOp = [];
        createOp.push(SC.Object.create({
            label: '_pointCreate'.loc(),
            key: 'point',
			active: function(key, value){
				if(value){
					Kloudgis.optionsController.activeUpdate(this);
				}
				return Kloudgis.mapControlController.get('createType') === this.get('key');
			}.property(),
			isEditable: YES
        }));
        createOp.push(SC.Object.create({
            label: '_lineCreate'.loc(),
            key: 'line',
			active: function(key, value){
				if(value){
					Kloudgis.optionsController.activeUpdate(this);
				}
				return Kloudgis.mapControlController.get('createType') === this.get('key');
			}.property(),
			isEditable: YES
        }));
        createOp.push(SC.Object.create({
            label: '_polygonCreate'.loc(),
            key: 'polygon',
			active: function(key, value){
				if(value){
					Kloudgis.optionsController.activeUpdate(this);
				}
				return Kloudgis.mapControlController.get('createType') === this.get('key');
			}.property(),
			isEditable: YES
        }));		
        this.set('content', createOp);
        this.buildPicker();
        this._picker.popup(anchor, SC.PICKER_POINTER, [3, 0, 1, 2, 2]);
    },

	activeUpdate: function(object){
		Kloudgis.mapControlController.set('createType', object.get('key'));
		this.selectObject(object);
		this.updateSelection();
	},

	selectionDidChanged: function() {
        this.invokeLater('updateSelection', 10);	
    }.observes('selection'),

	updateSelection: function(){
		var selection = this.get('selection').get('firstObject');
		if(selection){
			selection.setIfChanged('active', YES);
		}
		var len = this.get('length');
		var i;
		for(i=0; i < len; i++){
			if(this.objectAt(i) !== selection){
				this.objectAt(i).set('active', NO);
			}
		}
	},
	
	clearAll: function(){
		this.get('selection').set('length', 0);
		this.set('content', null);
		this._picker = null;
		this.set('activated', NO);
	},

    buildPicker: function() {
        this._picker = SC.PickerPane.create({
            layout: {
                width: 150,
                height: 130
            },
            contentView: SC.View.design({
                childViews: 'labelCreate listCreate index'.w(),
                labelCreate: SC.LabelView.design({
                    layout: {
                        left: 10,
                        right: 10,
                        top: 10,
                        height: 24,
                    },
                    value: '_createOption'.loc(),
                }),
                listCreate: SC.ListView.design({
                    layout: {
                        left: 10,
                        right: 10,
                        top: 34,
                        height: 60,
                    },
					classNames: 'menu'.w(),
					rowHeight: 20,
                    contentValueKey: "label",
                    canDeleteContent: NO,
                    contentBinding: 'Kloudgis.optionsController.arrangedObjects',
                    selectionBinding: 'Kloudgis.optionsController.selection',
					exampleView: Kloudgis.MenuListItemView
                }),

				index : SC.ButtonView.design({
					layout: {
                        left: 10,
                        right: 10,
                        top: 100,
                        height: 24,
                    },
					title: 'Full Index',
					action: function(){
						Kloudgis.toolbarController.fullIndex();
					}
				})				
            }),

			remove: function(){
				sc_super();
				this.destroy();
				Kloudgis.optionsController.clearAll();
			}
        });
    }
});