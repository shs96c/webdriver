package org.openqa.selenium;

import java.awt.Dimension;
import java.awt.Point;

public interface RenderedWebElement extends WebElement {
    /**
     * Is this element displayed or not? This method avoids the problem of having
     * to parse an element's "style" attribute.
     *
     * @return Whether or not the element is displayed
     */
    boolean isDisplayed();

    /**
     * Where on the page is the top left-hand corner of the rendered element?
     *
     * @return A point, containing the location of the top left-hand corner of the element
     */
      public Point getLocation();

    /**
     * What is the width and height of the rendered element?
     *
     * @return The size of the element on the page.
     */
      public Dimension getSize();
      
      /**
       * Drag and drop
       * 
       * @param moveRightBy how much to move to the right (negative for moving left)
       * @param moveDownBy how much to move to the bottom (negative for moving up)
       */
      void dragAndDropBy(int moveRightBy, int moveDownBy);
      
      /**
       * Drag and drop this element on top of the specified element 
       * 
       * @param element element to be dropped on. Only RenderedElement is supported
       */
      void dragAndDropOn(RenderedWebElement element);

      /**
		 * Get the value of a given CSS property. This is probably not going to
		 * return what you expect it to unless you've already had a look at the
		 * element using something like firebug. Seriously, even then you'll be
		 * lucky for this to work cross-browser. Colour values should be
		 * returned as hex strings, so, for example if the "background-color"
		 * property is set as "green" in the HTML source, the returned value
		 * will be "#008000"
		 * 
		 * @param propertyName
		 * @return The current, computed value of the property.
		 */
      String getValueOfCssProperty(String propertyName);
}
