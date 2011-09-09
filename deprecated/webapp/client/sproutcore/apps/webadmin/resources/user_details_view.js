sc_require('views/late_commit_text_field');
sc_require('resources/strings');
Webadmin.userDetailsView = SC.View.create({
    childViews: "labelName textName labelFull textFull labelEmail textEmail labelMore textMore".w(),
    labelName: SC.LabelView.design({
        layout: {
            "width": 90,
            "left": 50,
            "top": 30,
            "height": 20
        },
        classNames: 'labels'.w(),
        textAlign: SC.ALIGN_LEFT,
        value: "_users.Name".loc(),
    }),

    textName: Webadmin.LateCommitTextFieldView.design({
        layout: {
            "left": 50,
            "right": 50,
            "top": 50,
            "height": 20
        },
        valueBinding: 'Webadmin.activeUserController.name',
    }),

    labelFull: SC.LabelView.design({
        layout: {
            "width": 90,
            "left": 50,
            "top": 80,
            "height": 20
        },
        classNames: 'labels'.w(),
        textAlign: SC.ALIGN_LEFT,
        value: "_users.FullName".loc(),
    }),

    textFull: Webadmin.LateCommitTextFieldView.design({
        layout: {
            "left": 50,
            "right": 50,
            "top": 100,
            "height": 20
        },
        valueBinding: 'Webadmin.activeUserController.fullName',
    }),

    labelEmail: SC.LabelView.design({
        layout: {
            "width": 90,
            "left": 50,
            "top": 130,
            "height": 20
        },
        classNames: 'labels'.w(),
        textAlign: SC.ALIGN_LEFT,
        value: "_users.Email".loc(),
    }),

    textEmail: Webadmin.LateCommitTextFieldView.design({
        layout: {
            "left": 50,
            "right": 50,
            "top": 150,
            "height": 20
        },
        validator: "Email",
        valueBinding: 'Webadmin.activeUserController.email',
    }),

    labelMore: SC.LabelView.design({
        layout: {
            "width": 90,
            "left": 50,
            "top": 180,
            "height": 20
        },
        classNames: 'labels'.w(),
        textAlign: SC.ALIGN_LEFT,
        value: "_users.MoreInfo".loc(),
    }),

    textMore: Webadmin.LateCommitTextFieldView.design({
        layout: {
            "left": 50,
            "right": 50,
            "top": 200,
            "height": 60
        },
        isTextArea: YES,
        valueBinding: 'Webadmin.activeUserController.moreInfo',
    }),
});