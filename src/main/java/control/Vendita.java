package control;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.apache.commons.text.StringEscapeUtils;
import model.ProductBean;
import model.ProductModel;

@WebServlet("/Vendita")
public class Vendita extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Vendita() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductBean product = new ProductBean();
        product.setEmail((String) request.getSession().getAttribute("email"));
        
        String UPLOAD_DIRECTORY = request.getServletContext().getRealPath("/") + "img/productIMG/";
        if(ServletFileUpload.isMultipartContent(request)) {
            try {
                List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(new ServletRequestContext(request));
                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        String name = new File(item.getName()).getName();
                        item.write(new File(UPLOAD_DIRECTORY + File.separator + name));
                        product.setImmagine(name);
                    } else {
                        String fieldValue = StringEscapeUtils.escapeHtml4(item.getString());
                        if (item.getFieldName().compareTo("nome") == 0) {
                            product.setNome(fieldValue);
                        } else if (item.getFieldName().compareTo("prezzo") == 0) {
                            product.setPrezzo(Double.parseDouble(fieldValue));
                        } else if (item.getFieldName().compareTo("spedizione") == 0) {
                            product.setSpedizione(Double.parseDouble(fieldValue));
                        } else if (item.getFieldName().compareTo("tipologia") == 0) {
                            product.setTipologia(fieldValue);
                        } else if (item.getFieldName().compareTo("tag") == 0) {
                            product.setTag(fieldValue);
                        } else if (item.getFieldName().compareTo("descrizione") == 0) {
                            product.setDescrizione(fieldValue);
                        }
                    }
                }
                request.setAttribute("message", "File Uploaded Successfully");
            } catch (Exception ex) {
                request.setAttribute("message", "File Upload Failed due to " + ex);
            }
        } else {
            request.setAttribute("message", "Sorry this Servlet only handles file upload request");
        }
        ProductModel model = new ProductModel();
        try {
            model.doSave(product);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.getSession().setAttribute("refreshProduct", true);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
