/**
 * Created by zhangq on 2017/3/23.
 */


$(function () {
    /**
     * jquery 扩展提示信息框
     * '/static/admin-lte/plugins/toastr/toastr.min.js'
     * '/static/admin-lte/plugins/bootbox/bootbox.min.js'
     */
    $.extend({
        notify: {
            success: function (message, title,callback) {
                toastr.options.timeOut = 1000;
                $.isFunction(callback) && setTimeout(callback,toastr.options.timeOut);
                toastr.success(message, title  || "提示:");
            },
            error:function (message, title) {
                toastr.error(message, title  || "提示:");
            },
            info:function (message, title) {
                toastr.info(message, title  || "提示:");
            },
            warning:function (message, title) {
                toastr.warning(message, title  || "提示:");
            },
            confirm:function(message,callback){
                bootbox.setLocale("zh_CN");
                bootbox.confirm(message, callback);
            },
            alert:function(message,callback){
                bootbox.setLocale("zh_CN");
                bootbox.alert(message,callback);
            },
            prompt:function(message,callback){
                bootbox.setLocale("zh_CN");
                bootbox.prompt(message,callback);
            },
            custom:function(message,title){
                bootbox.dialog({
                    message: message,
                    title: title  || "提示:",
                    buttons: {
                        success: {
                            label: "Success!",
                            className: "btn-success",
                            callback: function() {
                                alert(success);
                            }
                        },
                        danger: {
                            label: "Danger!",
                            className: "btn-danger",
                            callback: function() {
                                alert(danger);
                            }
                        },
                        main: {
                            label: "Click ME!",
                            className: "btn-primary",
                            callback: function() {
                                alert(main);
                            }
                        }
                    }
                });
            }
        }
    });

});


(function ($) {
    /**
     * bootstrap dropdown-toggle 下拉选择jquery自定义扩展
     * <div class="btn-group pull-left" id="appGroups">
     * <button type="button"
     *  class="btn btn-primary dropdown-toggle"
     *  data-toggle="dropdown"
     *  aria-haspopup="true"
     *  aria-expanded="false"><i class="fa fa-angle-down"></i></button>
     *  <ul class="dropdown-menu">
     *      <li th:each="group,groupStat:${appGroups}">
     *      <a href="#" th:data-value="${group.id}" th:text="${group.name}"></a>
     *  </li>
     *  </ul>
     * </div>
     * <script id="selectTemplate" type="text/template">
     *  {@each options as item,index}
     *      <option value="${item.value}" data-code="${item.code}">${item.text}</option>
     *  {@/each}
     * </script>
     *
     * '/static/admin-lte/plugins/juicer/juicer.min.js' js模板引擎
     * @param callback
     */
    $.fn.dropdown = function (callback) {
        // 插件的具体内容放在这里
        this.each(function () {
            var $this = $(this);
            var $dropdown = $this.find(".dropdown-toggle");
            var $options = $this.find("ul.dropdown-menu>li");
            $options.on("click", function () {
                var opt = $(this);
                $dropdown.html(opt.find("a").html() + " <i class='fa fa-angle-down'></i>");
                $this.data("value", opt.find("a").data("value"));
                $this.data("index", opt.index());
                $this.data("text", opt.find("a").text());
                $this.data("placeholder", opt.find("a").text());
                $this.data("html", opt.find("a").html());
                if ($.isFunction(callback)) {
                    callback({
                        index: $this.data("index"),
                        value: $this.data("value"),
                        text: $this.data("text"),
                        html: $this.data("html")
                    });
                }
            });
        });
    };
})(jQuery);
