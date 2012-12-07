Ext.onReady(function()
{
    /* variables */
    var urlString = 'view.jsp';



    var infoStore = Ext.create('Ext.data.Store', {
        fields:[
                {name:'id', type:'int'},
                {name:'test_name', type:'string'},
                {name:'start', type:'string'},
                {name:'end', type:'string'},
                {name:'notes', type:'string'},
                {name:'rank', type:'int'},
                {name:'client_filename', type:'string'},
                {name:'server_filename', type:'string'},
                {name:'user', type:'string'}
            ],

        proxy:{
            type:'ajax',
            url:urlString,
            reader: {
                type:'json',
                root:'data'
            },
            extraParams:{
                task:'getInfo'
            }
        },

        autoLoad:true
    });

    var contextMenu = new Ext.menu.Menu({
        items: [
            {
                text: 'Show Chart',
                handler: function() {
                    updateChartsGrids(true);
                }
            },
            {
                text: 'Show Grid',
                handler: function() {
                    updateChartsGrids(false);
                }
            },
            {
                text:'Edit Test',
                handler: function() 
                {var record = infoGrid.getSelectionModel().getSelection()[0];

                    if(record)
                    {
                        editTest(record.get('id'), record.get('notes'), record.get('rank'));
                    }
                }
            },
            {
                text:'Delete Test',
                handler: function() 
                {
                    var record = infoGrid.getSelectionModel().getSelection()[0];

                    if(record)
                    {
                        deleteTest(record.get('id'), record.get('test_name'));
                    }
                }
            }
        ]
    });


    var infoGrid = Ext.create('Ext.grid.Panel', {
        store:infoStore,
        columns:[
            {text:'ID', dataIndex:'id', flex:1, hidden:true},
            {text:'Test Name', dataIndex:'test_name', flex:1},
            {text:'Start Time', dataIndex:'start', flex:1},
            {text:'End Time', dataIndex:'end', flex:1},
            {text:'Notes', dataIndex:'notes', flex:1},
            {text:'Rank', dataIndex:'rank', flex:1},
            {text:'Client Filename', dataIndex:'client_filename', flex:1},
            {text:'Server Filename', dataIndex:'server_filename', flex:1},
            {text:'User', dataIndex:'user', flex:1}
        ],

        viewConfig:{
            listeners: {
                itemcontextmenu: function(view, record, node, index, event) {
                    event.stopEvent();
                    contextMenu.showAt(event.getXY());
                    return false;
                }
            }
        },

        tbar: [
            {
                text: 'Load Test',
                handler : function()
                {
                    loadTest();
                }
            },
            {
                text:'Edit Test Names',
                handler: function()
                {
                    testNames();
                }
            }
        ]
    });




    Ext.create('Ext.container.Viewport', {
        layout:'border',

        items:[
            
            {
                title:'Navigation',
                region:'west',
                width:200,
                layout:'fit',
                html:navigation()
            },
            {
                //This is the panel that contains the tests and charts
                region:'center',
                items:[
                    {
                        title:'Test Info',
                        region:'north',
                        layout:'fit',
                        height:200,
                        items:infoGrid
                    },
                    {
                        xtype:'tabpanel',
                        activeTab:0,
                        region:'center',
                        height:400,

                        items:[
                            {
                                title:'Client',
                                id:'client',
                                layout:'fit'
                            },
                            {
                                title:'Server',
                                id:'server',
                                layout:'fit'
                            },
                            {
                                title:'Client & Server',
                                id:'client_server',
                                layout:'fit'
                            }
                        ]
                    }
                ]
            }
        ]
    });

    

    function updateChartsGrids(isChart)
    {
        var record = infoGrid.getSelectionModel().getSelection()[0];

        if(record)
        {
            var clientPanel = Ext.getCmp('client');
            updatePanel(isChart, clientPanel, record.get('id'), '1', 'Client');

            var serverPanel = Ext.getCmp('server');
            updatePanel(isChart, serverPanel, record.get('id'), '2', 'Server');

            var clientServerPanel = Ext.getCmp('client_server');
            updatePanel(isChart, clientServerPanel, record.get('id'), null, 'Client & Server');

            /*var store = createDataStore(urlString, {task:'getData', test_id:record.get('id'), node_id:'1'});
            var grid = createBasicGrid(store, 'Client');
            var chart = createBasicLineChart(store, 'Client', 100, 300);
            clientPanel.items.clear();
            clientPanel.add(grid);
            clientPanel.update();*/
        }
    }

    function updatePanel(isChart, panel, test_id, node_id, titleString)
    {
        var params = {task:'getData', test_id:test_id};

        if(node_id != null)
            params = {task:'getData', test_id:test_id, node_id:node_id};

        var store = createDataStore(urlString, params);
        var grid = createBasicGrid(store, titleString);
        var chart = createBasicLineChart(store, titleString, 100, 300);

        /*var gridPanel = Ext.create('Ext.panel.Panel',{
            region:'west',
            items:grid
        });

        var chartPanel = Ext.create('Ext.panel.Panel',{
            region:'east',
            items:chart
        });*/

        panel.items.clear();
        if(isChart)
            panel.add(chart);
        else
            panel.add(grid);
        panel.update();
    }


    function editTest(id, notes, rank)
    {
        var editForm = Ext.create('Ext.form.Panel', {
            bodyPadding: 5,

            url: urlString,
            baseParams:{ task:'editTest', id:id },

            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },

            items: [
                {
                    xtype:'textareafield',
                    fieldLabel: 'Notes',
                    name: 'notes',
                    value:notes
                },
                {
                    xtype: 'numberfield',
                    name: 'rank',
                    fieldLabel: 'Rank',
                    value: rank,
                    minValue: 0,
                    maxValue: 5
                }],

            buttons: [
                {
                    text: 'Save',
                    handler: function()
                    {
                        var form = this.up('form').getForm();

                        if (form.isValid())
                        {
                            form.submit({
                                success: function(form, action) {
                                   Ext.Msg.alert('Success', action.result.msg);
                                   infoStore.load();
                                   editWindow.close();
                                },
                                
                                failure: function(form, action) {
                                    Ext.Msg.alert('Failed', action.result.msg);
                                }
                            });
                        }
                    }
                }]
        });
        
        
        var editWindow = Ext.create('Ext.window.Window', {
            title: 'Edit Test',
            height: 200,
            width: 400,
            layout: 'fit',
            items: editForm
        }).show();

    }

    function deleteTest(id, name)
    {
        Ext.Msg.show({
            title: 'Delete Test?',
            msg: 'Are you sure you want to delete test, '+name+'?',
            buttons: Ext.Msg.YESNO,
            fn: function(btn){
                if(btn == 'yes')
                {
                    Ext.Ajax.request({
                        url: urlString,
                        params: {
                            task:'deleteTest',
                            id:id
                        },

                        success: function(response) {
                            Ext.Msg.alert('Result', response.responseText);
                            infoStore.load();
                        },

                        failure: function(response) {
                            Ext.Msg.alert('Error', response.responseText);
                        }
                    });
                }
            }
        });
    }


    function loadTest()
    {
        var loadForm = Ext.create('Ext.form.Panel', {
            bodyPadding: 5,

            url: urlString,
            baseParams:{ task:'loadTest' },

            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },

            items: [ createTestNameComboBox(), 
                createFilesComboBox('Client Filename', 'client_file'),
                createFilesComboBox('Server Filename', 'server_file')
            ],

            buttons: [
                {
                    text: 'Load',
                    handler: function()
                    {
                        var form = this.up('form').getForm();

                        if (form.isValid())
                        {
                            form.submit({
                                success: function(form, action) {
                                   Ext.Msg.alert('Success', action.result.msg);
                                   infoStore.load();
                                   loadTestWindow.close();
                                },

                                failure: function(form, action) {
                                    Ext.Msg.alert('Failed', action.result.msg);
                                }
                            });
                        }
                    }
                }]
        });


        var loadTestWindow = Ext.create('Ext.window.Window', {
            title: 'Edit Test',
            height: 200,
            width: 700,
            layout: 'fit',
            items: loadForm
        }).show();
    }



    /* This function is in charge of that little window that appears for editing test names */
    function testNames()
    {
        /* Store and Grid */
        var testNameStore = Ext.create('Ext.data.Store', {
            fields:[
                    {name:'id', type:'int'},
                    {name:'test_name', type:'string'}
                ],

            proxy:{
                type:'ajax',
                url:urlString,
                reader: {
                    type:'json',
                    root:'data'
                },
                extraParams:{
                    task:'getTestNames'
                }
            },

            autoLoad:true
        });

        var context = new Ext.menu.Menu({
            items: [
                {
                    text:'Edit Test Name',
                    handler: function()
                    {
                        var record = testNameGrid.getSelectionModel().getSelection()[0];

                        if(record)
                        {
                            addEditTestName(testNameStore, 'editTestName', 'Edit Test Name',
                                record.get('id'), record.get('test_name'));
                        }
                    }
                }
            ]
        });

        var testNameGrid = Ext.create('Ext.grid.Panel', {
            store:testNameStore,
            columns:[
                {text:'ID', dataIndex:'id', flex:1, hidden:true},
                {text:'Test Name', dataIndex:'test_name', flex:1}
            ],

            viewConfig:{
                listeners: {
                    itemcontextmenu: function(view, record, node, index, event) {
                        event.stopEvent();
                        context.showAt(event.getXY());
                        return false;
                    }
                }
            },

            tbar: [
                {
                    text: 'Add Test Name',
                    handler : function()
                    {
                        addEditTestName(testNameStore, 'addTestName', 'Add Test Name');
                    }
                }
            ]
        });

        /* Window */
        Ext.create('Ext.window.Window', {
            title: 'Edit Test Name',
            height: 400,
            width: 400,
            layout: 'fit',
            items: testNameGrid
        }).show();
    }


    function addEditTestName(store, taskString, titleString, id, name)
    {
        var editForm = Ext.create('Ext.form.Panel', {
            bodyPadding: 5,

            url: urlString,
            baseParams:{ task:taskString, id:id },

            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },

            items: [{
                    xtype:'textfield',
                    fieldLabel: 'Name',
                    name: 'name',
                    value:name
                }],

            buttons: [
                {
                    text: 'Submit',
                    handler: function()
                    {
                        var form = this.up('form').getForm();

                        if (form.isValid())
                        {
                            form.submit({
                                success: function(form, action) {
                                   Ext.Msg.alert('Success', action.result.msg);
                                   store.load();
                                   addEditWindow.close();
                                },

                                failure: function(form, action) {
                                    Ext.Msg.alert('Failed', action.result.msg);
                                }
                            });
                        }
                    }
                }]
        });


        var addEditWindow = Ext.create('Ext.window.Window', {
            title: titleString,
            height: 100,
            width: 400,
            layout: 'fit',
            items: editForm
        }).show();
    }

    function createFilesComboBox(fieldLabel, fieldName)
    {
        var fileStore = Ext.create('Ext.data.Store', {
            fields:[
                    {name:'name', type:'string'}
                ],

            proxy:{
                type:'ajax',
                url:urlString,
                reader: {
                    type:'json',
                    root:'data'
                },
                extraParams:{ task:'getFiles' }
            },

            autoLoad:true
        });

        var fileCombo = Ext.create('Ext.form.ComboBox', {
            fieldLabel: fieldLabel,
            store: fileStore,
            name:fieldName,
            displayField: 'name',
            valueField: 'name'
        });

        return fileCombo;
    }

});