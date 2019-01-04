function VpTree(option) {
    var domContainer=$('#'+option.id);
    var treeContainer;
    var treeData=option.data;
    init();
    function init() {
        console.info("ddddd=",treeData);
        domContainer.empty();
        domContainer.append('<div class="vp-tree-container"></div>');
        treeContainer=$('#'+option.id+' .vp-tree-container');
        create();
    }
    function create() {
        for(var i=0;i<treeData.length;i++){
            var nodeData=treeData[i];
            createNode(nodeData,null);
        }
        domContainer.find(".icon-triangle-right").bind("click",function (e) {
            e.stopPropagation();
            var target=$(e.target);
            var childrenContainer=target.parent().siblings();
            if(childrenContainer.length>0){
                if(childrenContainer.css("display")!='none'){
                    childrenContainer.hide();
                    target.parent().parent().removeClass("expand");
                }
                else{
                    childrenContainer.show();
                    target.parent().parent().addClass("expand");
                }
            }
        });
        domContainer.find(".vp-tree-node-content").bind("click",function (e) {
           e.stopPropagation();
            var target=$(e.currentTarget);
            var childrenContainer=target.siblings();
            domContainer.attr("val",target.attr("val"));
            vpEventTarget.fire({type:'change',id:option.id,source:'tree',data:target.attr("val")});
            $(".vp-tree-node-content").removeClass("select");
            target.addClass("select");
        });
    }
    function createNode(node,parent) {
        var currentParent=null;
        var nextParent;
        //console.info("ttttttt=",JSON.stringify(node));
        var valObj={};
        for(var key in node){
            if(key!="children")
                valObj[key]=node[key];
        }
        var html="<div class='vp-tree-node-container'>" +
            "<div class='vp-tree-node-content' val='"+JSON.stringify(valObj)+"'><span class='vp-tree-node-icon iconfont icon-triangle-right is-leaf'></span><span class='custormTreeNode'><span>"+node.label+"</span></span> </div>" +
            "</div>";
        if(node.children!=undefined && node.children!=null && node.children.length>0){
            html="<div class='vp-tree-node-container'>" +
                    "<div class='vp-tree-node-content' val='"+JSON.stringify(valObj)+"'><span class='vp-tree-node-icon iconfont icon-triangle-right'></span><span class=''><span>"+node.label+"</span></span> </div>"+
                    "<div class='vp-tree-children-container' style='display: none;'></div>"+
                "</div>";
        }
        if(parent==null){
            treeContainer.append(html);
            nextParent=treeContainer;
        }
        else {
            parent.append(html);
            nextParent=parent;
        }
        if(node.children!=undefined && node.children!=null && node.children.length>0){
            currentParent=nextParent.children(".vp-tree-node-container").children(".vp-tree-children-container");
            for(var i=0;i<node.children.length;i++){
                createNode(node.children[i],currentParent);
            }
        }
    }
}
window.VpTree=VpTree