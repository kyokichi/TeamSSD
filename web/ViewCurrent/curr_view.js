Ext.onReady(function()
{
    //global variable for the seconds; will probably change in the future to timestamp?
    var seconds = 0;

    var serverStore = createDataStore("curr_view.jsp", {first:'yes'});
    var clientStore = createDataStore("curr_view.jsp", {first:'yes'});

    var serverGrid = createBasicGrid(serverStore, "Server");
    var serverChart = createBasicLineChart(serverStore, "Server");
    var clientGrid = createBasicGrid(clientStore, "Client");
    var clientChart = createBasicLineChart(clientStore, "Client");
    

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
                        xtype:'tabpanel',
                        activeTab:0,
                        region:'north',
                        height:300,

                        items:[
                            {
                                title:'Chart',
                                layout:'fit',
                                items:clientChart
                            },
                            {
                                title:'Grid',
                                layout:'fit',
                                items:clientGrid
                            }
                        ]
                    },
                    {
                        xtype:'tabpanel',
                        activeTab:0,
                        region:'center',
                        height:300,

                        items:[
                            {
                                title:'Chart',
                                layout:'fit',
                                items:serverChart
                            },
                            {
                                title:'Grid',
                                layout:'fit',
                                items:serverGrid
                            }
                        ]
                    }
                ]
            }
        ]
    });


    Ext.TaskManager.start({
        run: function()
        {
            updateStore("curr_view.jsp", serverStore, 10, 2);
            updateStore("curr_view.jsp", clientStore, 10, 1);
            serverChart.redraw();
            clientChart.redraw();
            seconds = seconds + 2;
        },

        interval: 2000
    });


    function updateStore(urlString, store, limit, node)
    {
        Ext.Ajax.request({
            url: urlString,
            params:{ 
                seconds:seconds,
                node:node
            },

            success: function(response)
            {
                
                var json = Ext.decode(response.responseText);
                store.add(json);

                //console.log(dataStore.getCount());
                if(store.getCount() > limit)
                {
                    store.removeAt(0);
                }
                //dataStore.add( {power:45.6, time:"21:29:19"} );
                //var data = Ext.create('TestData', {
                //    power: 45.6,
                //   time: "21:29:19"
                //});
                //dataStore.data = json.data;
                //console.log(json.data);
                //dataStore.add(data);
                //dataStore.load();
                //console.log(dataStore);
            }
        });
    }

});