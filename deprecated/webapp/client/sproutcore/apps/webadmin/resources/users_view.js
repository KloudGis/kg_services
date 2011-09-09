// SproutCore ViewBuilder Design Format v1.0
// WARNING: This file is automatically generated.  DO NOT EDIT.  Changes you
// make to this file will be lost.
sc_require('views/late_commit_text_field');
sc_require('resources/strings');
Webadmin.usersView = SC.View.create({
    layout: {
        top: 0,
        left: 0,
        bottom: 0,
        right: 0
    },
    childViews: 'managerView bottomToolbar'.w(),
    managerView: SC.View.design({
        layout: {
            "centerX": 0,
            "centerY": 0,
            "width": 750,
            "height": 450
        },
        classNames: 'main-container'.w(),
        childViews: 'labelUsrView userScrollView addButton deleteButton detailsView voidView'.w(),

        labelUsrView: SC.LabelView.design({
            layout: {
                top: 20,
                height: 20,
                left: 10,
                width: 150
            },
            classNames: 'labels'.w(),
            textAlign: SC.ALIGN_LEFT,
            value: '_users.Users'.loc(),
        }),

        userScrollView: SC.ScrollView.design({
            layout: {
                "top": 40,
                "bottom": 40,
                "left": 10,
                "width": 218
            },
            backgroundColor: 'white',
            contentView: SC.ListView.design({
                contentBinding: 'Webadmin.usersController.arrangedObjects',
                selectionBinding: 'Webadmin.usersController.selection',
                contentValueKey: "name",
                canDeleteContent: YES,
                hasContentIcon: YES,
                contentIconKey: "icon",
                target: "Webadmin.usersController",
            })
        }),

        addButton: SC.ButtonView.design({
            layout: {
                bottom: 15,
                height: 24,
                left: 10,
                width: 34
            },
            icon: static_url("images/navigate_plus.png"),
            titleMinWidth: "0",
            title: "",
			toolTip: "_users.addToolTip".loc(),
            target: "Webadmin.usersController",
            action: "addUser"
        }),
        deleteButton: SC.ButtonView.design({
            layout: {
                bottom: 15,
                height: 24,
                left: 45,
                width: 34
            },
            isEnabledBinding: "Webadmin.usersController.deleteButtonEnabled",
            icon: static_url("images/navigate_minus.png"),
            titleMinWidth: "0",
            theme: "square",
            title: "",
			toolTip: "_users.deleteToolTip".loc(),
            target: "Webadmin.usersController",
            action: "deleteUser"
        }),

        voidView: SC.View.design({
            layout: {
                "left": 230,
                "right": 0,
                "top": 32,
                "bottom": 32
            },
            isVisibleBinding: "Webadmin.activeUserController.voidViewVisible",
            childViews: [
            SC.LabelView.design({
                layout: {
                    "width": 150,
                    "centerX": 0,
                    "centerY": 0,
                    "height": 20
                },
                textAlign: SC.ALIGN_CENTER,
                value: '_users.NoSelection'.loc(),
            }), ]
        }),
        detailsView: SC.View.design({
            layout: {
                "left": 240,
                "right": 10,
                "top": 40,
                "bottom": 40
            },
            isVisible: NO,
            isVisibleBinding: "Webadmin.activeUserController.detailsViewVisible",
            childViews: "tabView".w(),

            tabView: SC.TabView.design({
                nowShowing: 'Webadmin.userDetailsView',
                items: [{
                    title: '_users.Informations'.loc(),
                    value: "Webadmin.userDetailsView"
                },
                {
                    title: "_users.Security".loc(),
                    value: "Webadmin.userSecurityView"
                },
                ],
                itemTitleKey: 'title',
                itemValueKey: 'value',
            }),

        }),
    }),
    bottomToolbar: SC.ToolbarView.design({
        layout: {
            "bottom": 0,
            "left": 0,
            "right": 0,
            "height": 32
        },
        childViews: 'summaryView'.w(),
        summaryView: SC.LabelView.design({
            layout: {
                centerY: 0,
                height: 18,
                centerX: 0,
                width: 100
            },
            textAlign: SC.ALIGN_CENTER,
            valueBinding: "Webadmin.usersController.summary",
            value: ""
        })
    }),
/*
	labelDatasource: SC.LabelView.design({
        layout: {
            bottom: 40,
            height: 25,
            left: 10,
            right: 10
        },
		classNames: 'error-messages'.w(),
        textAlign: SC.ALIGN_CENTER,
        valueBinding: "Webadmin.usersController.datasource_message"
    }),*/
   
});
