package servlets;

import com.RapidFeedback.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This servlet is used to update the result before send final report
 *
 * Created by xiyang on 2020/4/19
 */
@WebServlet("/EditResultServlet")
public class EditResultServlet extends HttpServlet {

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditResultServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MysqlFunction dbFunction = new MysqlFunction();
        boolean mark_ACK = false;

        // get JSONObject from request
        JSONObject jsonReceive;
        BufferedReader reader = request.getReader();
        String str, wholeString = "";
        while ((str = reader.readLine()) != null) {
            wholeString += str;
        }
        System.out.println("Receive: " + wholeString);
        jsonReceive = JSON.parseObject(wholeString);

        // get values from received JSONObject
        String token = jsonReceive.getString("token");
        int projectId = jsonReceive.getIntValue("projectId");
        int studentId = jsonReceive.getIntValue("studentId");
        int type = jsonReceive.getIntValue("type");

        JSONObject jsonSend = new JSONObject();

        try{
            if(type == 0){
                // edit remark
//                String remarkSting = jsonReceive.getString("remark");
//                Remark remark = new Gson().fromJson(remarkSting, new TypeToken<Remark>(){}.getType());
//
//                dbFunction.updateResult(remark, projectId, studentId);

                // move to add result to make the android part easier
                // default return true
                jsonSend.put("ACK", true);
            } else if (type == 1) {
                // update final mark
                String finalListString = jsonReceive.getString("finalRemarkList");
                List<FinalRemark> finalRemarks = JSON.parseArray(finalListString, FinalRemark.class);
                Project project = dbFunction.getProjectAllInfo(projectId);
                ProjectStudent projectStudent = dbFunction.getProjectStudent(studentId, projectId);
                List<FinalRemark> dbFinalRemarks = dbFunction.getFinalRemark(studentId,projectId);
                double finalScore = getFinalMark(project, projectStudent, finalRemarks, dbFinalRemarks);

                // assume every time android part will pass all Criterion
                boolean inDB = (dbFinalRemarks != null && dbFinalRemarks.size() > 0);
                dbFunction.updateFinalMark(projectId, studentId, finalRemarks, finalScore, inDB);

                jsonSend.put("ACK", true);
            } else {
                jsonSend.put("ACK", false);
                jsonSend.put("Message", "no such type");
            }
        } catch (Exception ex){
            jsonSend.put("ACK", false);
            jsonSend.put("Message", ex.getMessage());
        }

        PrintWriter output = response.getWriter();
        output.print(jsonSend.toJSONString());
        System.out.println("Send: " + jsonSend.toJSONString());
    }

    /**
     * description: get all project info with id
     *
     * @return the average mark of this criterion
     * @param project the info of project
     * @param projectStudent the info of student in project
     * @param finalRemarkList the final reamrk of every criterion from client
     * @param dbFinalRemarks the final reamrk of every criterion in db
     */
    private double getFinalMark(Project project, ProjectStudent projectStudent,
                                List<FinalRemark> finalRemarkList, List<FinalRemark> dbFinalRemarks) throws Exception {
        double totalMark = 0d;
        double sumMark = 0d;

        ArrayList<Criterion> criteriaList = project.getCriterionList();
        for(Criterion criterion : criteriaList){
            boolean contain = false;
            totalMark += criterion.getMaximumMark();
            for(FinalRemark finalRemark : dbFinalRemarks){
                if(finalRemark.getCriterionId() == criterion.getId()){
                    sumMark += finalRemark.getFinalScore();
                    contain = true;
                }
            }
            for(FinalRemark finalRemark : finalRemarkList){
                if(finalRemark.getCriterionId() == criterion.getId()){
                    sumMark += finalRemark.getFinalScore();
                    contain = true;
                }
            }
            // in assessments then continue next
            if(contain){
                continue;
            }
            // there are final mark then use it
            sumMark += getAverageCriterionMark(projectStudent.getRemarkList(), criterion.getId());
        }
        return sumMark * (100.0 / totalMark);
    }

    /**
     * description: get all project info with id
     *
     * @return the average mark of this criterion
     * @param remarkList the remarkList of this project
     * @param criterionId the id of criterion
     */
    private double getAverageCriterionMark(ArrayList<Remark> remarkList, int criterionId) {
        double sumMark = 0;
        double markers = remarkList.size();
        for (int i = 0; i < markers; i++) {
            Remark remark = remarkList.get(i);
            for (int j = 0; j < remark.getAssessmentList().size(); j++) {
                if (remark.getAssessmentList().get(j).getCriterionId() == criterionId) {
                    sumMark += remark.getAssessmentList().get(j).getScore();
                }
            }
        }
        double avgMark = sumMark / markers;
        BigDecimal bigDecimal = new BigDecimal(avgMark);
        avgMark = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return avgMark;
    }
}
