/*******************************************************************************
 * Copyright (c) 2014-2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
scout.events = {

  /**
   * @returns the x coordinate where the event happened, works for touch events as well.
   */
  pageX: function(event) {
    if (!scout.objects.isNullOrUndefined(event.pageX)) {
      return event.pageX;
    }
    return event.originalEvent.touches[0].pageX;
  },

  /**
   * @returns the y coordinate where the event happened, works for touch events as well.
   */
  pageY: function(event) {
    if (!scout.objects.isNullOrUndefined(event.pageY)) {
      return event.pageY;
    }
    return event.originalEvent.touches[0].pageY;
  },

  touchdown: function(touch, suffix) {
    return this.touchOrMouse(touch, 'touchstart', 'mousedown', suffix);
  },

  touchmove: function(touch, suffix) {
    return this.touchOrMouse(touch, 'touchmove', 'mousemove', suffix);
  },

  touchendcancel: function(touch, suffix) {
    return this.touchOrMouse(touch, 'touchend touchcancel', 'mouseup', suffix);
  },

  touchOrMouse: function(touch, touchevent, mouseevent, suffix) {
    suffix = suffix || '';
    if (suffix) {
      suffix = '.' + suffix;
    }
    if (touch) {
      return touchevent + suffix;
    }
    return mouseevent + suffix;
  },

  isTouchEvent: function(event) {
    return event && scout.strings.startsWith(event.type, 'touch');
  },

  fixTouchEvent: function(event) {
    if (this.isTouchEvent(event)) {
      var touches = event.touches || (event.originalEvent ? event.originalEvent.touches : null);
      var touch = touches ? touches[0] : null;
      if (touch) {
        // Touch events may contain fractional values, while mouse events should not
        // - https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent/pageX
        // - https://www.chromestatus.com/features/6169687914184704
        event.pageX = Math.round(touch.pageX);
        event.pageY = Math.round(touch.pageY);
      }
    }
  },

  /**
   * @returns an object containing passive: true if the browser supports passive event listeners, otherwise returns false.
   */
  passiveOptions: function(event) {
    var options = false;
    if (scout.device.supportsPassiveEventListener()) {
      options = {
        passive: true
      };
    }
    return options;
  },

  /**
   * Listens for scroll events and executes startHandler on first event. It then regularly checks for further scroll events and executes endHandler if no event has fired since a certain amount of time and the user has released his finger.
   * If he does not release his finger the endHandler won't be called even if the pane has stopped scrolling.
   */
  onScrollStartEndDuringTouch: function($elem, startHandler, endHandler) {
    var scrollTimeout;
    var started = false;
    var touchend = false;
    var scrollHandler = function(event) {
      // Execute once on first scroll event (and not as soon as user touches the pane because he might not even want to scroll)
      if (!started) {
        startHandler();
        started = true;
      }

      clearTimeout(scrollTimeout);
      // Check some ms later if a scroll event has occured in the mean time.
      // If yes it (probably) is still scrolling. If no it (probably) is not scrolling anymore -> call handler
      checkLater();
    };

    var touchEndHandler = function(event) {
      touchend = true;
      checkLater();
    };

    function checkLater() {
      clearTimeout(scrollTimeout);
      scrollTimeout = setTimeout(function() {
        if (touchend) {
          // Only stop processing if user released the finger
          removeHandlers();
          endHandler();
        }
      }, 50);
    }

    function removeHandlers() {
      $elem.off('scroll', scrollHandler);
      $elem.document().off('touchend touchcancel', touchEndHandler);
    }

    $elem.on('scroll', scrollHandler);
    // Make sure handler is executed and scroll listener removed if no scroll event occurs
    $elem.document().one('touchend touchcancel', touchEndHandler);
  }
};