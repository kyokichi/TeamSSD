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
                    updateChartsGrids();
                }
            },
            {
                text:'Edit Test',
                handler: function() {

                }
            },
            {
                text:'Delete Test',
                handler: function() {
                    
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
        }
    });




    Ext.create('Ext.container.Viewport', {
        layout:'border',

        items:[
            
            {
                title:'Navigation',
                region:'west',
                width:200,
                layout:'fit'
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

    

    function updateChartsGrids()
    {
        var record = infoGrid.getSelectionModel().getSelection()[0];

        if(record)
        {
            var clientPanel = Ext.getCmp('client');
            updatePanel(clientPanel, record.get('id'), '1', 'Client');

            var serverPanel = Ext.getCmp('server');
            updatePanel(serverPanel, record.get('id'), '2', 'Server');

            var clientServerPanel = Ext.getCmp('client_server');
            updatePanel(clientServerPanel, record.get('id'), null, 'Client & Server');

            /*var store = createDataStore(urlString, {task:'getData', test_id:record.get('id'), node_id:'1'});
            var grid = createBasicGrid(store, 'Client');
            var chart = createBasicLineChart(store, 'Client', 100, 300);
            clientPanel.items.clear();
            clientPanel.add(grid);
            clientPanel.update();*/
        }
    }

    function updatePanel(panel, test_id, node_id, titleString)
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
        panel.add(chart);
        panel.update();
    }

});