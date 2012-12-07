Ext.onReady(function()
{
    /* variables */
    var urlString = 'schedule.jsp';

    


    /* Here is the form for editing Schedules */
    var scheduleForm = Ext.create('Ext.form.Panel', {
        bodyPadding: 5,
        height:300,
        width:300,
        title:'Schedule a Job',

        url: urlString,

        layout: 'anchor',
        defaults: {
            anchor: '100%'
        },

        items: [
            {
                xtype:'textfield',
                fieldLabel:'Schedule Name',
                name:'test_name',
                allowBlank:false
            },
            {
                xtype:'datefield',
                fieldLabel:'Start Date',
                name:'date',
                format:'Y-m-d',
                allowBlank:false
            },
            {
                xtype:'timefield',
                fieldLabel:'Start Time',
                name:'time',
                format:'H:i',
                allowBlank:false
            },
            {
                xtype: 'numberfield',
                fieldLabel: 'Recording Length (in seconds)',
                name:'seconds',
                minValue: 0,
                maxValue: 1000,
                allowBlank:false
            }],

        buttons: [
            {
                text: 'Add Job',
                handler: function()
                {
                    var form = this.up('form').getForm();

                    if (form.isValid())
                    {
                        form.submit({
                            success: function(form, action) {
                               Ext.Msg.alert('Success', action.result.msg);
                            },

                            failure: function(form, action) {
                                Ext.Msg.alert('Failed', action.result.msg);
                            }
                        });
                    }
                }
            }]
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
                region:'center',
                items:[scheduleForm]
            }
        ]
    });


});