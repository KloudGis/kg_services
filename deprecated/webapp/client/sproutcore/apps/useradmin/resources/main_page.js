// ==========================================================================
// Project:   Useradmin - mainPage
// Copyright: ©2011 My Company, Inc.
// ==========================================================================
/*globals Useradmin */

// This page describes the main user interface for your application.  
sc_require('views/user_list_item')
sc_require('views/user_selected')
Useradmin.mainPage = SC.Page.design({

    userSelectedView: SC.outlet('mainPane.mainSplit.bottomRightView.contentView'),

	detailsView: SC.outlet('mainPane.mainSplit.bottomRightView.contentView.detailsView'),

    mainPane: SC.MainPane.design({

        defaultResponder: Useradmin.statechart,
        childViews: 'mainSplit'.w(),

        mainSplit: SC.SplitView.design({
            layerId: 'main-split',
            layout: {
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            },

            layoutDirection: SC.LAYOUT_HORIZONTAL,
            defaultThickness: 230,
            topLeftMinThickness: 220,
            // topLeftMaxThickness: 400,
            dividerThickness: 4,
            dividerView: SC.SplitDividerView.design({
                layout: {}
            }),

            //users list with search and bottom toolbar
            topLeftView: SC.View.design({

                childViews: 'topToolbar listContainer bottomToolbar'.w(),

                topToolbar: SC.ToolbarView.design({
                    anchorLocation: SC.ANCHOR_TOP,
                    classNames: 'toolbar-users'.w(),
                    childViews: 'searchField reloadButton'.w(),

                    //search field
                    searchField: SC.TextFieldView.design({
                        classNames: 'search'.w(),
                        layout: {
                            top: 5,
                            height: 24,
                            width: 180,
                            left: 5,
                        },
                        valueBinding: 'Useradmin.usersController.userFilter',
                        keyDown: function(evt) {
                            SC.Logger.warn('User filter Key Down! ' + evt.which);
                            var ret = sc_super();
                            var which = evt.which;
                            if (which === 13 && !evt.isIMEInput) {
                                //ENTER pressed
                                Useradmin.usersController.userFilterChanged();
                            } else if (which === 27) {
                                //ESC pressed
                                this.invokeLater(this.clearField, 10);
                            }
                            return ret;
                        },
                        hint: "_filter".loc(),

                        clearField: function() {
                            this.set('value', '');
                        }

                    }),

                    reloadButton: Useradmin.ToolbarButtonView.design({
                        layerId: 'reload-button',
                        layout: {
                            centerY: 0,
                            height: 24,
                            right: 3,
                            width: 24
                        },
                        toolTip: "_users_reload".loc(),
                        icon: sc_static('images/reload.png'),
                        iconSelected: sc_static('images/reload_h.png'),
                        action: 'flushAndReloadUsers'
                    })

                }),

                listContainer: SC.ContainerView.design({
                    layout: {
                        top: 38,
                        bottom: 38,
                        right: 3,
                        left: 3
                    },
                    classNames: 'round-edges-for-list'.w(),
                    contentView: SC.ScrollView.design({
                        layout: {
                            top: 5,
                            bottom: 5
                        },
                        contentView: SC.ListView.design({
                            rowHeight: 24,
                            contentValueKey: "label",
                            canDeleteContent: YES,
                            exampleView: Useradmin.UserListItemView,
                            contentBinding: SC.Binding.multiple('Useradmin.usersController.arrangedObjects').oneWay(),
                            selectionBinding: SC.Binding.multiple('Useradmin.usersController.selection'),

                            selectionDidChanged: function() {
                                if (!SC.none(Useradmin.userSelectedController.get('content'))) {
                                    var index = Useradmin.usersController.indexOf(Useradmin.userSelectedController.get('content'));
                                    if (index >= 0) {
                                        this.scrollToContentIndex(index);
                                    }
                                }
                            }.observes('Useradmin.userSelectedController.content')
                        })
                    }),

                }),

                bottomToolbar: SC.ToolbarView.design({
                    anchorLocation: SC.ANCHOR_BOTTOM,
                    classNames: 'toolbar-users'.w(),
                    childViews: 'addButton deleteButton optionsButton countLabel'.w(),

                    addButton: Useradmin.ToolbarButtonView.design({
                        layerId: 'add-button',
                        layout: {
                            centerY: 0,
                            height: 24,
                            left: 10,
                            width: 24
                        },
                        toolTip: "_users_add".loc(),
                        icon: sc_static('images/add.png'),
                        iconSelected: sc_static('images/add_h.png'),
                        action: 'addUser'
                    }),

                    deleteButton: Useradmin.ToolbarButtonView.design({
                        layerId: 'delete-button',
                        layout: {
                            centerY: 0,
                            height: 24,
                            left: 44,
                            width: 24
                        },
                        toolTip: "_users_delete".loc(),
                        icon: sc_static('images/delete.png'),
                        iconSelected: sc_static('images/delete_h.png'),
                        action: 'deleteUser'
                    }),

                    optionsButton: Useradmin.ToolbarButtonView.design({
                        layerId: 'options-button',
                        layout: {
                            centerY: 0,
                            height: 24,
                            left: 79,
                            width: 24
                        },
                        toolTip: "_users_options".loc(),
                        icon: sc_static('images/options.png'),
                        iconSelected: sc_static('images/options_h.png')
                    }),

                    countLabel: SC.LabelView.design({
                        layerId: 'user-count',
                        layout: {
                            centerY: 0,
                            height: 20,
                            right: 10,
                            left: 105
                        },
                        textAlign: null,
                        valueBinding: 'Useradmin.usersController.countLabel',
                    })
                }),
            }),

            //user selection view
            bottomRightView: SC.ContainerView.design({
				layerId: 'user-details-container',
                contentView: Useradmin.UserSelectedView.design({
                    //not visible by default
                    isVisible: NO
                })
            })
        })
    }),

	profileView: Useradmin.ProfileView.design({})
});
