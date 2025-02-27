dojo.provide("struts.widget.StrutsTreeSelector");

dojo.require("dojo.widget.TreeSelector");

dojo.widget.defineWidget(
  "struts.widget.StrutsTreeSelector",
  dojo.widget.TreeSelector, {
  widgetType : "StrutsTreeSelector",
  
  selectedNotifyTopics : "",
  collapsedNotifyTopics : "",
  expandedNotifyTopics : "",
  
  selectedNotifyTopicsArray : null,
  collapsedNotifyTopicsArray : null,
  expandedNotifyTopicsArray : null,
  
  eventNamesDefault: {
    select : "select",
    destroy : "destroy",
    deselect : "deselect",
    dblselect: "dblselect", // select already selected node.. Edit or whatever
    expand: "expand",
    collapse: "collapse"
  },
  
  initialize: function () {
    struts.widget.StrutsTreeSelector.superclass.initialize.apply(this);
    
    if(!dojo.string.isBlank(this.selectedNotifyTopics)) {
      this.selectedNotifyTopicsArray = this.selectedNotifyTopics.split(",");
    }

    if(!dojo.string.isBlank(this.selectedNotifyTopics)) {
      this.collapsedNotifyTopicsArray = this.collapsedNotifyTopics.split(",");
    }
    
    if(!dojo.string.isBlank(this.selectedNotifyTopics)) {
      this.expandedNotifyTopicsArray = this.expandedNotifyTopics.split(",");
    }
  },
  
  listenTree: function(tree) {
    dojo.event.topic.subscribe(tree.eventNames.collapse, this, "collapse");
    dojo.event.topic.subscribe(tree.eventNames.expand, this, "expand");
    struts.widget.StrutsTreeSelector.superclass.listenTree.apply(this, [tree]);
  },
  
  unlistenTree: function(tree) {
    dojo.event.topic.unsubscribe(tree.eventNames.collapse, this, "collapse");
    dojo.event.topic.unsubscribe(tree.eventNames.expand, this, "expand");
    struts.widget.StrutsTreeSelector.superclass.unlistenTree.apply(this, [tree]);
  },

  publishTopics : function(topics, node) {
    if(topics != null) {
      for(var i = 0; i < topics.length; i++) {
        var topic = topics[i];
        if(!dojo.string.isBlank(topic)) {
          try {
            dojo.event.topic.publish(topic, node);
          } catch(ex) {
            dojo.debug(ex);
          }
        }
      }
    }
  },
  
  select:function (message) {
    var node = message.source;
    var e = message.event;
    if (this.selectedNode === node) {
      if (e.ctrlKey || e.shiftKey || e.metaKey) {
        this.deselect();
        return;
      }
      dojo.event.topic.publish(this.eventNames.dblselect, {node:node});
      return;
    }
    if (this.selectedNode) {
      this.deselect();
    }
    this.doSelect(node);
    
    this.publishTopics(this.selectedNotifyTopicsArray, {node: node});
  },
  
  expand: function(message) {
    var node = message.source;
    this.publishTopics(this.expandedNotifyTopicsArray, {node: node});
  },
  
  collapse: function(message) {
    var node = message.source;
    this.publishTopics(this.collapsedNotifyTopicsArray, {node: node});
  }
});