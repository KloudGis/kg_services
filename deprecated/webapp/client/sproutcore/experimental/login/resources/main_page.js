// ==========================================================================
// Project:   Login - mainPage
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Login */

// This page describes the main user interface for your application.  
Login.mainPage = SC.Page.design({

    mainPane: SC.MainPane.design({
        childViews: 'contentView'.w(),

        contentView: SC.ContainerView.design({
            layout: {
                top: 70
            }
        })
    }),

    loginPane: SC.PanelPane.design({
        layout: {
            width: 360,
            height: 125,
            centerX: 0,
            centerY: 0
        },
        classNames: ['login-pane'],

        contentView: SC.View.design({
            childViews: 'username password loginButton loadingImage errorMessage'.w(),

            username: SC.View.design({
                layout: {
                    left: 17,
                    right: 14,
                    top: 17,
                    height: 26
                },
                childViews: 'label field'.w(),

                label: SC.LabelView.design({
                    layout: {
                        left: 0,
                        width: 77,
                        height: 18,
                        centerY: 0
                    },

                    value: '_Username',
                    localize: YES,
                    textAlign: SC.ALIGN_RIGHT
                }),

                field: SC.TextFieldView.design({
                    layout: {
                        width: 230,
                        height: 22,
                        right: 3,
                        centerY: 0
                    },
                    valueBinding: 'Login.loginController.username'
                })
            }),

            password: SC.View.design({
                layout: {
                    left: 17,
                    right: 14,
                    top: 45,
                    height: 26
                },
                childViews: 'label field'.w(),

                label: SC.LabelView.design({
                    layout: {
                        left: 0,
                        width: 77,
                        height: 18,
                        centerY: 0
                    },

                    value: '_Password',
                    localize: YES,
                    textAlign: SC.ALIGN_RIGHT
                }),

                field: SC.TextFieldView.design({
                    layout: {
                        width: 230,
                        height: 22,
                        right: 3,
                        centerY: 0
                    },

                    isPassword: YES,
                    valueBinding: 'Login.loginController.password'
                })
            }),

            loginButton: SC.ButtonView.design({
                layout: {
                    height: 24,
                    width: 80,
                    bottom: 17,
                    right: 17
                },
                title: '_Login',
                localize: YES,
                isDefault: YES,

                target: 'Login.firstResponder',
                action: 'login'
            }),

            loadingImage: SC.ImageView.design({
                layout: {
                    width: 16,
                    height: 16,
                    bottom: 20,
                    right: 110
                },
                value: sc_static('images/loading.gif'),
                useImageCache: NO,
                isVisibleBinding: 'Login.loginController.loggingIn'
            }),

            errorMessage: SC.LabelView.design({
                layout: {
                    height: 40,
                    width: 230,
                    right: 120,
                    bottom: 7
                },
                classNames: ['error-message'],
                valueBinding: 'Login.loginController.errorMessage'
            })
        }),

        //necessary to render the login panel edges properly
        render: function(context, firstTime) {
            if (context.needsContent) {
                this.renderChildViews(context, firstTime);
            }
        }
    })

});
