dojo.provide("struts.widget.BindEvent");

dojo.require("struts.widget.Bind");

dojo.widget.defineWidget(
  "struts.widget.BindEvent",
  struts.widget.Bind, {
  widgetType : "BindEvent",

  sources: "",

  postCreate : function() {
    struts.widget.BindEvent.superclass.postCreate.apply(this);
    var self = this;

    if(!dojo.string.isBlank(this.events) && !dojo.string.isBlank(this.sources)) {
      var eventsArray = this.events.split(",");
      var sourcesArray = this.sources.split(",");

      if(eventsArray && this.domNode) {
        //events
        dojo.lang.forEach(eventsArray, function(event) {
          //sources
          dojo.lang.forEach(sourcesArray, function(source) {
            var sourceObject = dojo.byId(source);
            if(sourceObject) {
              dojo.event.connect(sourceObject, event, function(evt) {
                evt.preventDefault();
                evt.stopPropagation();
                self.reloadContents();
              });
            }
          });
        });
      }
    }
  }
});