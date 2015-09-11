/**
 * Turns an element into a Converge activity stream of the current user.
 * Depends on humane.js for displaying pretty dates.
 * 
 * @version 1.0
 * @author Allan Lykke Christensen
 */
(function ($) {

    var stream = $('<ul class="activity-stream"></ul>');
    var loading = $('<div class="image"><img class="activity-stream-spinner" src="/converge/images/loading.gif" /></div>');
    var moreLink = $('<a id="activity-stream-more">More</a>');

    $.fn.convergeActivityStream = function () {
        var parent = this;

        parent.append(stream);
        parent.append(moreLink);
        parent.append(loading);

        var page = $.fn.convergeActivityStream.defaults.page;
        var size = $.fn.convergeActivityStream.defaults.size;

        // Bind the "click" event of the moreLink to loading the next page of the activity stream
        moreLink.bind("click", function (event) {
            page++;
            $.fn.convergeActivityStream.load(stream, page, size);
        });

        $.fn.convergeActivityStream.load(stream, page, size);

        return this;
    };

    $.fn.convergeActivityStream.load = function (ul, page, size) {
        moreLink.hide();
        loading.show();

        var service_request = $.fn.convergeActivityStream.defaults.service_url + "?page=" + page + "&size=" + size;
        $.getJSON(service_request, function (data) {
            $.each(data.items, function (key, activity) {
                var li = $('<li class="activity"></li>');
                li.attr('data-activity-id', activity.id);

                var item = $('<div class="stream-item-content"></div>');
                li.append(item);

                var imageInfo = activity.author.image;
                var image = $('<div class="image">' +
                        '<img src="/converge' + imageInfo.url + '" />' +
                        '</div>');
                item.append(image);

                var content = $('<div class="content"></div>');
                item.append(content);

                var user = $('<div class="activity-row">' +
                        '<span class="user-name">' +
                        '<a class="screen-name" title="' + activity.author.displayName + '">' + activity.author.displayName + '</a>' +
                        '</span>' +
                        '</div>');
                content.append(user);

                var message = $('<div class="activity-row">' +
                        '<div class="textcontent">' + activity.content + '</div>' +
                        '</div>');
                content.append(message);

                var time = $('<div class="activity-row">' +
                        '<a href="' + activity.url + '" class="timestamp">' +
                        '<span class="pretty-date" title="' + activity.published + '">' + activity.published + '</span>' +
                        '</a>' +
                        '</div>');
                content.append(time);

                li.appendTo(ul);
            });

            $('.pretty\\-date').humane_dates();
            loading.hide();
            moreLink.show();
        });
    };

    $.fn.convergeActivityStream.defaults = {
        service_url: "/converge-ws/service/activitystream",
        size: 10,
        page: 0
    };

}(jQuery));
