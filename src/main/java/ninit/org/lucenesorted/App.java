package ninit.org.lucenesorted;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        RandomDocumentIterator iterator = new RandomDocumentIterator(100000);
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
