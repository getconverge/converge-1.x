/**
 * Turns an element into a Converge activity stream of the current user.
 * Depends on humane.js for displaying pretty dates.
 * 
 * @version 1.0
 * @author Allan Lykke Christensen
 */
(function ($) {
    $.fn.convergeActivityStream = function () {
        var parent = this;

        var image = $('<div class="image">' +
                '<img class="activity-stream-spinner" src="/converge/images/loading.gif" />' +
                '</div>');
        parent.append(image);

        var ul = $('<ul class="activity-stream"></ul>');
        parent.append(ul);

        $.getJSON("/converge-ws/service/activitystream", function (data) {
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

            $('.activity\\-stream\\-spinner').hide();
            $('.pretty\\-date').humane_dates();
        });
    };
}(jQuery));
