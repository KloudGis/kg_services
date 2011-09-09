sc_require('resources/strings');
Webadmin.welcomeView = SC.View.create({
    layout: {
        "centerX": 0,
        "centerY": 0,
        "width": 800,
        "height": 600
    },
    childViews: [
    SC.LabelView.design({
        layout: {
            "width": 150,
            "centerX": 0,
            "centerY": 0,
            "height": 20
        },
        textAlign: SC.ALIGN_CENTER,
        value: 'Web Administrator',
    }), SC.ButtonView.design({
        layout: {
            centerX: 0,
            centerY: 100,
            height: 24,
            width: 100
        },
        titleMinWidth: "0",
        title: "See the Users",
        target: "Webadmin.activeAppController",
        action: "switchToUsers"
    })]
});
