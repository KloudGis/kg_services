sc_require('views/late_commit_text_field');
sc_require('resources/strings');
Webadmin.userSecurityView = SC.View.create({
    childViews: "changePasswordButton labelExpire textExpire labelExpireMessage labelRolesView rolesScrollView addRoleButton removeRoleButton".w(),

    labelExpire: SC.LabelView.design({
        layout: {
            "width": 90,
            "left": 50,
            "top": 30,
            "height": 20
        },
        classNames: 'labels'.w(),
        textAlign: SC.ALIGN_LEFT,
        value: "_users.Expire".loc(),
    }),

    textExpire: Webadmin.LateCommitTextFieldView.design({
        layout: {
            "left": 50,
            "width": 150,
            "top": 50,
            "height": 20
        },
        hint: "_users.ExpireHint".loc(),
        valueBinding: 'Webadmin.activeUserController.expireDate',
    }),

    changePasswordButton: SC.ButtonView.design({
        layout: {
            "left": 250,
            "width": 150,
            "top": 50,
            "height": 24
        },
        theme: "capsule",
        title: "_users.ChangePassword".loc(),
        target: "Webadmin.activeUserController",
        action: "changePassword"
    }),

    labelExpireMessage: SC.LabelView.design({
        layout: {
            "right": 20,
            "left": 20,
            "top": 120,
            "height": 20
        },
        classNames: 'messages'.w(),
        textAlign: SC.ALIGN_CENTER,
        valueBinding: "Webadmin.activeUserController.expireMessage"
    }),

    labelRolesView: SC.LabelView.design({
        layout: {
            top: 150,
            height: 20,
            left: 50,
            width: 180
        },
        classNames: 'labels'.w(),
        textAlign: SC.ALIGN_LEFT,
        valueBinding: "Webadmin.activeRolesController.summary",
    }),

    rolesScrollView: SC.ScrollView.design({
        layout: {
            "top": 170,
            "bottom": 28,
            "left": 50,
            "width": 180
        },
        backgroundColor: 'white',
        contentView: SC.ListView.design({
            contentBinding: 'Webadmin.activeRolesController',
			contentValueKey: 'role_name',
            selectionBinding: 'Webadmin.activeRolesController.selection',
            canDeleteContent: YES,
            hasContentIcon: NO,
        })
    }),

    addRoleButton: SC.ButtonView.design({
        layout: {
            bottom: 3,
            height: 24,
            left: 50,
            width: 34
        },
        icon: static_url("images/navigate_plus.png"),
        titleMinWidth: "0",
        title: "",
        toolTip: "_users.addRolesToolTip".loc(),
        target: "Webadmin.activeRolesController",
        action: "addRole"
    }),

    removeRoleButton: SC.ButtonView.design({
        layout: {
            bottom: 3,
            height: 24,
            left: 85,
            width: 34
        },
        isEnabledBinding: "Webadmin.activeRolesController.removePrivilegesEnabled",
        icon: static_url("images/navigate_minus.png"),
        titleMinWidth: "0",
        theme: "square",
        title: "",
        toolTip: "_users.removeRolesToolTip".loc(),
        target: "Webadmin.activeRolesController",
        action: "removeRole"
    })
});
