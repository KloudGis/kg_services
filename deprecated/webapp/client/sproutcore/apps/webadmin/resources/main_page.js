
sc_require('views/late_commit_text_field');
sc_require('resources/strings');
Webadmin.mainPage = SC.Page.design({
    mainPane: SC.MainPane.design({

        layout: {
            "top": 0,
            "left": 0,
            "right": 0,
            "bottom": 0
        },
        childViews: 'mainView topToolbar'.w(),

        mainView: SC.ContainerView.design({
            layout: {
                "top": 32,
                "left": 0,
                "right": 0,
                "bottom": 0
            },
            nowShowingBinding: "Webadmin.activeAppController.activePage",
        }),
        topToolbar: SC.ToolbarView.design({
            layout: {
                "top": 0,
                "left": 0,
                "right": 0,
                "height": 32
            },
            childViews: 'labelView buttonUsers buttonGroups'.w(),
            labelView: SC.LabelView.design({
                layout: {
                    centerY: 0,
                    height: 24,
                    centerX: 0,
                    width: 200
                },
                textAlign: SC.ALIGN_CENTER,
                controlSize: SC.LARGE_CONTROL_SIZE,
                fontWeight: SC.BOLD_WEIGHT,
                valueBinding: "Webadmin.activeAppController.activeTitle",
            }),
            buttonUsers: SC.ButtonView.design({
                layout: {
                    top: 4,
                    height: 32,
                    left: 10,
                    width: 46,
                },
                titleMinWidth: '0',
                icon: "sc-icon-user-24",
                title: '',
				toolTip: "_users.toggleToolTip".loc(),
                buttonBehavior: SC.TOGGLE_BEHAVIOR,
                valueBinding: "Webadmin.activeAppController.usersSelected",
            }),
            buttonGroups: SC.ButtonView.design({
                layout: {
                    top: 4,
                    height: 32,
                    left: 66,
                    width: 46,
                },
                titleMinWidth: '0',
                buttonBehavior: SC.TOGGLE_BEHAVIOR,
                icon: "sc-icon-group-24",
                title: '',
				toolTip: "_groups.toggleToolTip".loc(),
				valueBinding: "Webadmin.activeAppController.groupsSelected",
            }),
        }),
    }),
    pageName: "Webadmin.mainPage",
});
