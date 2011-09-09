// ==========================================================================
// Project:   Webadmin.Group Fixtures
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Webadmin */

sc_require('models/group');

Webadmin.Group.FIXTURES = [

  // TODO: Add your data fixtures here.
  // All fixture records must have a unique primary key (default 'guid').  See 
  // the example below.

   { guid: 1,
     name: "Group1",
     members: [1,2] },
  
   { guid: 2,
     name: "Group2",
     members: [3] },

];
