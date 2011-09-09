sc_require('views/late_commit_text_field');
sc_require('resources/strings');
Webadmin.groupsView = SC.View.create({
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
        childViews: 'labelGrpView groupScrollView addButton deleteButton detailsView voidView'.w(),

        labelGrpView: SC.LabelView.design({
            layout: {
                top: 20,
                height: 20,
                left: 10,
                width: 150
            },
            classNames: 'labels'.w(),
            textAlign: SC.ALIGN_LEFT,
            value: '_groups.Groups'.loc(),
        }),

        groupScrollView: SC.ScrollView.design({
            layout: {
                "top": 40,
                "bottom": 40,
                "left": 10,
                "width": 218
            },
            backgroundColor: 'white',
            contentView: SC.ListView.design({
                contentBinding: 'Webadmin.groupsController.arrangedObjects',
                selectionBinding: 'Webadmin.groupsController.selection',
                contentValueKey: "name",
                canDeleteContent: YES,
                hasContentIcon: YES,
                contentIconKey: "icon",
                target: "Webadmin.groupsController",
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
            toolTip: "_groups.addToolTip".loc(),
            target: "Webadmin.groupsController",
            action: "addGroup"
        }),

        deleteButton: SC.ButtonView.design({
            layout: {
                bottom: 15,
                height: 24,
                left: 45,
                width: 34
            },
            isEnabledBinding: "Webadmin.groupsController.deleteButtonEnabled",
            icon: static_url("images/navigate_minus.png"),
            titleMinWidth: "0",
            theme: "square",
            title: "",
            toolTip: "_groups.deleteToolTip".loc(),
            target: "Webadmin.groupsController",
            action: "deleteGroup"
        }),

        voidView: SC.View.design({
            layout: {
                "left": 230,
                "right": 0,
                "top": 32,
                "bottom": 32
            },
            isVisibleBinding: "Webadmin.activeGroupController.voidViewVisible",
            childViews: [
            SC.LabelView.design({
                layout: {
                    "width": 150,
                    "centerX": 0,
                    "centerY": 0,
                    "height": 20
                },
                textAlign: SC.ALIGN_CENTER,
                value: '_groups.NoSelection'.loc(),
            }), ]
        }),
        detailsView: SC.View.design({
            layout: {
                "left": 240,
                "right": 0,
                "top": 30,
                "bottom": 20
            },
            isVisible: NO,
            isVisibleBinding: "Webadmin.activeGroupController.detailsViewVisible",
            childViews: "labelName textName labelParent parentGroupView labelMembersView membersScrollView labelPrivilegesView privilegesScrollView addMembersButton removeMembersButton addPrivilegesButton removePrivilegesButton".w(),
            labelName: SC.LabelView.design({
                layout: {
                    "width": 90,
                    "left": 50,
                    "top": 5,
                    "height": 20
                },
                classNames: 'labels'.w(),
                textAlign: SC.ALIGN_LEFT,
                value: "_groups.Name".loc(),
            }),

            textName: SC.TextFieldView.design({
                layout: {
                    "left": 50,
                    "right": 50,
                    "top": 25,
                    "height": 20
                },
                valueBinding: 'Webadmin.activeGroupController.name',
            }),

            labelParent: SC.LabelView.design({
                layout: {
                    "width": 90,
                    "left": 50,
                    "top": 50,
                    "height": 20
                },
                classNames: 'labels'.w(),
                textAlign: SC.ALIGN_LEFT,
                value: "_groups.Parent".loc(),
            }),

            parentGroupView: Webadmin.LateCommitTextFieldView.design({
                layout: {
                    "left": 50,
                    "width": 150,
                    "top": 70,
                    "height": 24
                },
                valueBinding: 'Webadmin.activeGroupController.parent_group_label',
            }),

            labelMembersView: SC.LabelView.design({
                layout: {
                    top: 105,
                    height: 20,
                    left: 50,
                    width: 120
                },
                classNames: 'labels'.w(),
                textAlign: SC.ALIGN_LEFT,
                valueBinding: "Webadmin.activeMembersController.summary",
            }),

            membersScrollView: SC.ScrollView.design({
                layout: {
                    "top": 125,
                    "bottom": 25,
                    "left": 50,
                    "width": 180
                },
                backgroundColor: 'white',
                contentView: SC.ListView.design({
                    contentBinding: 'Webadmin.activeMembersController',
                    selectionBinding: 'Webadmin.activeMembersController.selection',
                    contentValueKey: "name",
                    canDeleteContent: YES,
                    hasContentIcon: YES,
                    contentIconKey: "icon",
                })
            }),

            addMembersButton: SC.ButtonView.design({
                layout: {
                    bottom: 0,
                    height: 24,
                    left: 50,
                    width: 34
                },
                icon: static_url("images/navigate_plus.png"),
                titleMinWidth: "0",
                title: "",
                toolTip: "_groups.addMemberToolTip".loc(),
                target: "Webadmin.activeMembersController",
                action: "addMembers"
            }),

            removeMembersButton: SC.ButtonView.design({
                layout: {
                    bottom: 0,
                    height: 24,
                    left: 85,
                    width: 34
                },
                isEnabledBinding: "Webadmin.activeMembersController.removeMembersEnabled",
                icon: static_url("images/navigate_minus.png"),
                titleMinWidth: "0",
                theme: "square",
                title: "",
                toolTip: "_groups.removeMemberToolTip".loc(),
                target: "Webadmin.activeMembersController",
                action: "removeMembers"
            }),

            labelPrivilegesView: SC.LabelView.design({
                layout: {
                    top: 105,
                    height: 20,
                    right: 50,
                    width: 180
                },
                classNames: 'labels'.w(),
                textAlign: SC.ALIGN_LEFT,
                valueBinding: "Webadmin.activePrivilegesController.summary",
            }),

            privilegesScrollView: SC.ScrollView.design({
                layout: {
                    "top": 125,
                    "bottom": 25,
                    "right": 50,
                    "width": 180
                },
                backgroundColor: 'white',
                contentView: SC.ListView.design({
                    contentBinding: 'Webadmin.activePrivilegesController',
                    selectionBinding: 'Webadmin.activePrivilegesController.selection',
                    contentValueKey: "name",
                    canDeleteContent: YES,
                    hasContentIcon: NO,
                })
            }),

            addPrivilegesButton: SC.ButtonView.design({
                layout: {
                    bottom: 0,
                    height: 24,
                    right: 197,
                    width: 34
                },
                icon: static_url("images/navigate_plus.png"),
                titleMinWidth: "0",
                title: "",
                toolTip: "_groups.addPrivilegesToolTip".loc(),
                target: "Webadmin.activePrivilegesController",
                action: "addPrivileges"
            }),

            removePrivilegesButton: SC.ButtonView.design({
                layout: {
                    bottom: 0,
                    height: 24,
                    right: 162,
                    width: 34
                },
                isEnabledBinding: "Webadmin.activePrivilegesController.removePrivilegesEnabled",
                icon: static_url("images/navigate_minus.png"),
                titleMinWidth: "0",
                theme: "square",
                title: "",
                toolTip: "_groups.removePrivilegesToolTip".loc(),
                target: "Webadmin.activePrivilegesController",
                action: "removePrivileges"
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
            valueBinding: "Webadmin.groupsController.summary",
            value: ""
        })
    }),

    /*   labelDatasource: SC.LabelView.design({
        layout: {
            bottom: 40,
            height: 25,
            left: 10,
            right: 10
        },
        classNames: 'error-messages'.w(),
        textAlign: SC.ALIGN_CENTER,
        valueBinding: "Webadmin.groupsController.datasource_message"
    }),*/

});
