/* returns a data store based off of parameters and a url string */
function createDataStore(urlString, params)
{
    var store = Ext.create('Ext.data.Store', {
        fields:[
                {name:'power', type:'float'},
                {name:'time', type:'int'}
            ],

        proxy:{
            type:'ajax',
            url:urlString,
            reader: {
                type:'json',
                root:'data'
            },
            extraParams:params
        },

        autoLoad:true
    });

    return store;
}

/* returns a test info store */
function createInfoStore(urlString, params)
{
    var store = Ext.create('Ext.data.Store', {
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
            extraParams:params
        },

        autoLoad:true
    });

    return store;
}


/* returns a basic server/client grid */
function createBasicGrid(store, titleString)
{
    var grid = Ext.create('Ext.grid.Panel', {
        title:titleString,
        store:store,
        columns:[
            {text:'Power (watts)', dataIndex:'power', flex:1},
            {text:'Time (seconds)', dataIndex:'time', flex:1}
        ]
    });

    return grid;
}

/* returns a basic server/client line chart */
function createBasicLineChart(store, titleString, min, max)
{


    var chart = Ext.create('Ext.chart.Chart', {
        title:titleString,
        store:store,

        axes:[
            {
                title:'Power (watts)',
                type:'Numeric',
                position:'left',
                fields:['power'],
                minimum:min,
                maximum:max
            },
            {
                title:'Time (seconds)',
                type:'Category',
                position:'bottom',
                fields:['time']
            }
        ],

        series:[
            {
                type:'line',
                xField:'time',
                yField:'power'
            }
        ]
    });

    return chart;
}

/* returns an test info grid */
function createInfoGrid(store, titleString)
{
    var grid = Ext.create('Ext.grid.Panel', {
        title:titleString,
        store:store,
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
        ]
    });

    return grid;
}


/* takes an object and returns a panel */
function createPanel(item, renderString, height, width)
{
    var panel = Ext.create('Ext.panel.Panel',
    {
        layout:'fit',
        items:[ item ],
        renderTo:renderString,
        height:height,
        width:width
    });

    return panel;
}