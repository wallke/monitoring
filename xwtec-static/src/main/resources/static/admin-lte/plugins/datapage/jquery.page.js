/**
 * Created by face on 2014/9/12.
 */

(function($) {
/**
 * page library
 */
"use strict";

    String.prototype.format = function(args) {
        var result = this;
        if (arguments.length > 0) {
            if (arguments.length == 1 && typeof (args) == "object") {
                for (var key in args) {
                    if(args[key]!=undefined){
                        var reg = new RegExp("({" + key + "})", "g");
                        result = result.replace(reg, args[key]);
                    }
                }
            }
            else {
                for (var i = 0; i < arguments.length; i++) {
                    if (arguments[i] != undefined) {
                        var reg= new RegExp("({)" + i + "(})", "g");
                        result = result.replace(reg, arguments[i]);
                    }
                }
            }
        }
        return result;
    }


    $.fn.page = function(options){

        var options = $.extend({
            index:15,
            maxIndex:89,
            callback:null,
            parameter:"page",
            style:null,//pagination-lg pagination-sm
            preText:"上一页",
            nextText:"下一页",
            size:10,
            sizeParameter:"size",
            sizes:[10,25,50,100],
            align:'right'
        }, options);


        var render = function(obj){
            options.index = Math.round(options.index);
            options.maxIndex = Math.round(options.maxIndex);
            if (options.maxIndex < 1 || isNaN(options.maxIndex) || isNaN(options.index)) {
                $(obj).empty();
                return;
            }

            var $pageBox = $("<ul></ul>")
                .addClass("pagination")
                .addClass(options.style || "");

            var $current = $("<li><a href=\"javascript:void(0)\">" + options.index + "<span class='sr-only'>(current)</span></a></li>").addClass("active");

            $("<li><a href=\"javascript:void(0)\">" + options.preText + "</a></li>")
                .addClass(options.index == 1 ? "disabled" : "")
                .appendTo($pageBox);

            if (options.maxIndex <= 10) {
                for (var i = 1; i <= options.maxIndex; i++) {
                    if (i != options.index)
                        $("<li><a href=\"javascript:void(0)\">" + i + "</a></li>").appendTo($pageBox);

                    else {
                        $current.appendTo($pageBox);
                    }
                }
            }
            else if(options.index < 5){
                for(var i=1;i<=6;i++){
                    if (i != options.index)
                        $("<li><a href=\"javascript:void(0)\">" + i + "</a></li>").appendTo($pageBox);

                    else {
                        $current.appendTo($pageBox);

                    }
                }
                $("<li><a href=\"javascript:void(0)\">…</a></li>").addClass("disabled").appendTo($pageBox);
                $("<li><a href=\"javascript:void(0)\">" + options.maxIndex + "</a></li>").appendTo($pageBox);

            }else if(options.maxIndex- options.index < 4){
                $("<li><a href=\"javascript:void(0)\">" + 1 + "</a></li>").appendTo($pageBox);
                $("<li><a href=\"javascript:void(0)\">…</a></li>").addClass("disabled").appendTo($pageBox);
                for(var i=options.maxIndex-5;i<=options.maxIndex;i++)
                {
                    if (i != options.index)
                        $("<li><a href=\"javascript:void(0)\">" + i + "</a></li>").appendTo($pageBox);

                    else {
                        $current.appendTo($pageBox);
                    }
                }
            }else{
                $("<li><a href=\"javascript:void(0)\">" + 1 + "</a></li>").appendTo($pageBox);
                $("<li><a href=\"javascript:void(0)\">…</a></li>").addClass("disabled").appendTo($pageBox);
                for(var i=options.index-2;i<=options.index+2;i++)
                {
                    if (i != options.index)
                        $("<li><a href=\"javascript:void(0)\">" + i + "</a></li>").appendTo($pageBox);

                    else {
                        $current.appendTo($pageBox);
                    }
                }
                $("<li><a href=\"javascript:void(0)\">…</a></li>").addClass("disabled").appendTo($pageBox);
                $("<li><a href=\"javascript:void(0)\">" + options.maxIndex + "</a></li>").appendTo($pageBox);
            }
            $("<li><a href=\"javascript:void(0)\">"+options.nextText+"</a></li>")
                .addClass(options.index==options.maxIndex ? "disabled" : "").appendTo($pageBox);


            var $activeTag = $pageBox.find("li").filter(function(index) {
                return !$(this).hasClass("disabled") && !$(this).hasClass("active");
            }).each(function(){
                var aIndex = 0;
                if($(this).text() == options.preText){
                    aIndex =options.index - 1;
                }else if($(this).text() == options.nextText){
                    aIndex =options.index + 1;
                }else{
                    aIndex =$(this).text();
                }

                if($.isFunction(options.callback) ){
                    $(this).on("click",function(){
                        options.index = aIndex;
                        render(obj);
                        options.callback(aIndex);
                    });
                }else{
                    var baseUrl = location.href.replace(/#/g,"");
                    if (baseUrl.indexOf(options.parameter +"=") > -1) {
                        baseUrl = baseUrl.replace(new RegExp('('+ options.parameter +'=)(\\d+)',"gmi"), "$1" + aIndex);
                    }else{
                        baseUrl += ((baseUrl.indexOf("?") === -1) ? "?"+options.parameter +"=" : "&"+options.parameter +"=") + aIndex;
                    }
                    $(this).find("a").attr("href",baseUrl);
                }

            });


            //

            // 禁用每页记录数选择
            if(options.size && options.sizes && false){

                var $select = $("<select class='form-control input-sm' />");
                $.each(options.sizes,function(i,v){
                    var $option = $("<option />").val(v).text(v);
                    if(v == options.size){
                        $option.attr("selected","selected");
                    }
                    $select.append($option);
                });

                $select.change(function(){
                    var baseUrl = location.href.replace(/#/g,"");
                    if (baseUrl.indexOf(options.sizeParameter +"=") > -1) {
                        baseUrl = baseUrl.replace(new RegExp('('+ options.sizeParameter +'=)(\\d+)',"gmi"), "$1" + $(this).val());
                    }else{
                        baseUrl += ((baseUrl.indexOf("?") === -1) ? "?"+options.sizeParameter +"=" : "&"+options.sizeParameter +"=") + $(this).val();
                    }
                    location.href = baseUrl;
                });

                $(obj).append($("<div class='col-sm-11' />").css("text-align",options.align).html($pageBox));
                $(obj).append( $("<div class='col-sm-1' />").html($select));
            }else{
                $(obj).css("text-align","right").html($pageBox);
                //$pageBox.html($(obj).html())
            }

        }

        this.each(function() {
            render(this);
        });

    };

})(jQuery);