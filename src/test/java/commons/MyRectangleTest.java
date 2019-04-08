package commons;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyRectangleTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Test
  public void insertTest() {
    MyRectangle queryRect = new MyRectangle("(-73.969601, 40.762531, -73.969601, 40.762531)");
    MyRectangle bbox = new MyRectangle("[-73.971444, 40.762827, -73.971444, 40.762827]");
    MyRectangle intersectRect = bbox.intersect(queryRect);
    if (intersectRect == null)
      Util.println("Null");
    else
      Util.println(intersectRect);
  }

  @Test
  public void intersectTest() {
    MyRectangle rectangle1 = new MyRectangle(-180, -90, 180, 90);
    MyRectangle rectangle2 = new MyRectangle(
        "(113.50180238485339, 22.187478257613602, 113.56607615947725,22.225842149771214)");
    Util.println(rectangle1.intersect(rectangle2));
  }

}
