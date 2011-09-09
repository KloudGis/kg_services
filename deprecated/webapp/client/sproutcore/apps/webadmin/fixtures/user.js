// ==========================================================================
// Project:   Webadmin.User Fixtures
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

sc_require('models/user');

Webadmin.User.FIXTURES = [

// TODO: Add your data fixtures here.
// All fixture records must have a unique primary key (default 'guid').  See 
// the example below.
{
    "guid": "1",
    "name": "toto",
    "fullname": "the full toto",
	"group" : 1,
},
{
    "guid": "2",
    "name": "toto2",
    "fullname": "the full toto2",
	"group": 2
},
];
