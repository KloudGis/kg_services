sc_require('views/category_list_item')
Useradmin.UserSelectedView = SC.View.extend({
	
	layerId: 'user-selected-view',
	
	childViews: 'topToolbar buttonSelectedToolbar messageView listCategoryView detailsView'.w(),
	
	topToolbar: SC.ToolbarView.design({
        anchorLocation: SC.ANCHOR_TOP,
        classNames: 'toolbar-users'.w(),
	}),
	
	buttonSelectedToolbar: SC.ToolbarView.design({
		layout:{
			top: 32,
			height: 16,
			left:0,
			right:0
		},
		
		childViews: 'labelView'.w(),
		
		labelView: SC.LabelView.design({
			layerId: 'selected-category-label',
			layout:{left:10},
			valueBinding: 'Useradmin.categorySelectedController.label'
		})
    }),

	messageView: SC.View.design({
		layout:{
			top: 50,
			height: 40,
			left:0,
			right:0
		},
		
		childViews: 'labelView'.w(),
		
		labelView: SC.View.design({
			layerId: 'user-selected-message-label',
		//	useStaticLayout: YES,
			render: function(context, firstTime){
				context.push('<Label>Free Account</Label>');
			}
		})
	
	}),
	
	listCategoryView: SC.ListView.design({
		classNames: 'category-list-view'.w(),
		layout:{top:100,left:30, width: 200, bottom: 30},
		//backgroundColor: 'red',
		rowHeight: 40,
        contentValueKey: "label",
        canDeleteContent: NO,
        exampleView: Useradmin.CategoryListItemView,
        contentBinding: SC.Binding.multiple('Useradmin.categoryController.arrangedObjects').oneWay(),
        selectionBinding: SC.Binding.multiple('Useradmin.categoryController.selection'),		
	}),
	
	detailsView: SC.ContainerView.design({
		layout:{top:100,left:250, width: 550, bottom: 30},
		//backgroundColor: 'green',
		
	})	
	
});