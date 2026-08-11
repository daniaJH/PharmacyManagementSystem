/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pharmacymanagementsystem;

import com.mycompany.pharmacymanagementsystem.dao.DashboardDAO;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class DashboardController implements Initializable {

    // الحاوية الوسطى لعرض الصفحات داخل الداشبورد
    @FXML private StackPane contentArea;

    // متغير لحفظ عناصر الداشبورد الأصلية (البطاقات والشارت) عند الفتح
    private Node mainDashboardContent;
@FXML
private Label username;

@FXML
private Label type;
@FXML
private Button purchasesButton;
    // عناصر البطاقات (Labels)
    @FXML private Label lblTotalProducts;
    @FXML private Label lblTotalSales;
    @FXML private Label lblTotalStock;
    @FXML private ListView<String> alertListView;
    @FXML private Label welcomeLabel;
    private static boolean isFirstLogin = true;
@FXML
private Button reportsButton;
    // عنصر الرسم البياني (BarChart)
    @FXML private BarChart<String, Number> weeklySalesChart;

    private final DashboardDAO dashboardDAO = new DashboardDAO();
private void applyPermissions() {

    boolean isOwner =
            "Owner".equalsIgnoreCase(UserSession.getUserType());

    // أشياء خاصة بالمالك فقط
    if (employeesButton != null) {
        employeesButton.setVisible(isOwner);
        employeesButton.setManaged(isOwner);
    }

    if (reportsButton != null) {
        reportsButton.setVisible(isOwner);
        reportsButton.setManaged(isOwner);
    }
}
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (isFirstLogin) {
            welcomeLabel.setVisible(true);
            welcomeLabel.setManaged(true);
            isFirstLogin = false;
        } else {
            welcomeLabel.setVisible(false);
            welcomeLabel.setManaged(false);
        }
loadUserInfo();
applyPermissions();

loadCardsData();
setupWeeklyProfitChart();
setupNotifications();

        // حفظ محتوى السنتر الأصلي (الداشبورد) أول ما يشتغل البرنامج
        if (contentArea != null && !contentArea.getChildren().isEmpty()) {
            mainDashboardContent = contentArea.getChildren().get(0);
        }
    }

    private void loadCardsData() {
        if (lblTotalProducts != null) {
            lblTotalProducts.setText(String.valueOf(dashboardDAO.getTotalProducts()));
        }
        if (lblTotalSales != null) {
            lblTotalSales.setText(String.format("$%.2f", dashboardDAO.getTotalSales()));
        }
        if (lblTotalStock != null) {
            lblTotalStock.setText(String.valueOf(dashboardDAO.getTotalStockCount()));
        }
    }
@FXML
private Button logoutButton;
    @FXML private NumberAxis yAxis; // أضيفي هذا المتغير فوق مع الـ @FXML
private void loadUserInfo() {

    if (username != null) {
        username.setText(UserSession.getFirstName());
    }

    if (type != null) {
        type.setText(UserSession.getUserType());
    }
}
@FXML
private void handleopenemployee(ActionEvent event) {

    try {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Employee.fxml")
        );

        Parent root = loader.load();

        contentArea.getChildren().setAll(root);

    } catch (IOException e) {

        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Unable to open Employee page.");
        alert.showAndWait();
    }
}
  @FXML
    private Button employeesButton;
@FXML
private void handlelogout(ActionEvent event) {

    try {
        // مسح بيانات المستخدم الحالي
        UserSession.clear();

        // فتح شاشة تسجيل الدخول
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Parent root = loader.load();

        
        // الحصول على الـ Stage الحالي
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        // تغيير الـ Scene إلى Login
        stage.setScene(new Scene(root));
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
    }
}
private void setupWeeklyProfitChart() {
    if (weeklySalesChart == null) return;

    // 1. ضبط المحور العمودي يدوياً لضمان ظهور الأرقام والخطوط
    yAxis = (NumberAxis) weeklySalesChart.getYAxis();
    yAxis.setAutoRanging(false); // إيقاف النطاق التلقائي الضيق
    yAxis.setLowerBound(0);
    yAxis.setUpperBound(100);    // الحد الأقصى الأولي
    yAxis.setTickUnit(20);       // خطوة التدريج (0, 20, 40, 60, 80, 100)

    weeklySalesChart.setAnimated(false);
    weeklySalesChart.getData().clear();

    XYChart.Series<String, Number> series = new XYChart.Series<>();
   series.setName("Sales ($)");

    Map<String, Double> profitData = dashboardDAO.getWeeklyProfitData();
    String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    double maxVal = 100;

    for (String day : days) {
        // إذا كانت القيمة من الداتابيز 0، سنضع 5 مؤقتاً للتأكد من ظهور العمود، أو القيمة الحقيقية
        double val = (profitData != null && profitData.containsKey(day)) ? profitData.get(day) : 0.0;
        
        if (val > maxVal) maxVal = val; // تعديل أقصى حد تلقائياً إذا كانت المبيعات أعلى
        
        series.getData().add(new XYChart.Data<>(day, val));
    }

    // إعادة ضبط أعلى حد حسب أكبر قيمة
    yAxis.setUpperBound(Math.max(100, maxVal + 20));

    weeklySalesChart.getData().add(series);
}

// دالة Home: لا تقومي بإعادة استدعاء setupWeeklyProfitChart() هنا نهائياً
@FXML
public void handleHomeButton(ActionEvent event) {

    if (mainDashboardContent != null) {

        // إظهار الـ Dashboard
        contentArea.getChildren().setAll(mainDashboardContent);

        // تحديث البطاقات
        loadCardsData();

        // تحديث الرسم البياني
        setupWeeklyProfitChart();

        // تحديث التنبيهات
        setupNotifications();
    }
}

@FXML
void handleopenreports(ActionEvent event) {

    try {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Reports.fxml")
        );

        Parent root = loader.load();

        contentArea.getChildren().setAll(root);

    } catch (IOException e) {

        e.printStackTrace();

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(
                "Unable to open Reports."
        );

        alert.showAndWait();
    }
}


    // دالة فتح واجهة المنتجات داخل الـ StackPane
    @FXML
    public void handleProducts(ActionEvent event) {
        try {
            Parent productsView = FXMLLoader.load(getClass().getResource("/fxml/products.fxml"));
            contentArea.getChildren().setAll(productsView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
@FXML
public void handleSalesButton(ActionEvent event) {
    try {
        // تحميل ملف المبيعات
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SALES.fxml"));
        Parent salesView = loader.load();
        
        // عرض الواجهة في المنطقة المخصصة (contentArea)
        contentArea.getChildren().setAll(salesView);
        
    } catch (IOException e) {
        e.printStackTrace();
        // إظهار رسالة خطأ إذا لم يتم العثور على الملف
    }
}
    // دالة زر Home لإعادة عرض محتوى الداشبورد الرئيسي داخل الـ StackPane
    @FXML
    
   

    private void setupNotifications() {
        if (alertListView == null) return;

        alertListView.setItems(dashboardDAO.getNotificationMessages());

        alertListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);

                    String baseStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 12px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-cursor: hand; ";

                    if (item.contains("⚠️")) {
                        String normalStyle = baseStyle + "-fx-background-color: #fde8e8; -fx-text-fill: #9b1c1c; -fx-border-color: #f8b4b4; -fx-effect: dropshadow(three-pass-box, rgba(239, 68, 68, 0.1), 5, 0, 0, 2);";
                        String hoverStyle  = baseStyle + "-fx-background-color: #fbd5d5; -fx-text-fill: #9b1c1c; -fx-border-color: #f87171; -fx-effect: dropshadow(three-pass-box, rgba(239, 68, 68, 0.25), 8, 0, 0, 4);";
                        
                        setStyle(normalStyle);
                        setOnMouseEntered(e -> setStyle(hoverStyle));
                        setOnMouseExited(e -> setStyle(normalStyle));

                    } else if (item.contains("📦")) {
                        String normalStyle = baseStyle + "-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-border-color: #fcd34d; -fx-effect: dropshadow(three-pass-box, rgba(245, 158, 11, 0.1), 5, 0, 0, 2);";
                        String hoverStyle  = baseStyle + "-fx-background-color: #fde68a; -fx-text-fill: #92400e; -fx-border-color: #fbbf24; -fx-effect: dropshadow(three-pass-box, rgba(245, 158, 11, 0.25), 8, 0, 0, 4);";

                        setStyle(normalStyle);
                        setOnMouseEntered(e -> setStyle(hoverStyle));
                        setOnMouseExited(e -> setStyle(normalStyle));

                    } else {
                        String normalStyle = baseStyle + "-fx-background-color: #def7ec; -fx-text-fill: #03543f; -fx-border-color: #84e1bc;";
                        setStyle(normalStyle);
                    }
                }
            }
        });
    }
    @FXML
private void loadView(String fxmlFile) {
    try {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/" + fxmlFile)
        );

        Parent view = loader.load();

        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

@FXML
void handleopenprofiledetial(ActionEvent event) {
    loadView("Profileinfo.fxml");
}
@FXML
void handlecompanyOpen(ActionEvent event) {
    loadView("Manufactur.fxml");
}
   @FXML
    void handleopencustbored(ActionEvent event) {
loadView("Customers.fxml");
    }
    
@FXML
private void handleopenstocktaking(ActionEvent event) {

    try {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Stock Taking.fxml")
        );

        Parent root = loader.load();

        contentArea.getChildren().setAll(root);

    } catch (IOException e) {

        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(
                "Unable to open Stock Taking page."
        );
        alert.showAndWait();
    }
}

@FXML
void Purchasesopenbtn(ActionEvent event) {

    try {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Purchases.fxml")
        );

        Parent purchasesView = loader.load();

        contentArea.getChildren().setAll(purchasesView);

    } catch (IOException ex) {

        ex.printStackTrace();

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(
                "Unable to open Purchases page."
        );

        alert.showAndWait();
    }
}
    public static void resetWelcomeState() {
        isFirstLogin = true;
    }
  
}