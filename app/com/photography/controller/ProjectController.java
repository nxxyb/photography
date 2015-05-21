package com.photography.controller;

import java.io.File;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.photography.exception.ErrorCode;
import com.photography.exception.ErrorMessage;
import com.photography.exception.ServiceException;
import com.photography.mapping.FileGroup;
import com.photography.mapping.FileInfo;
import com.photography.mapping.Project;
import com.photography.mapping.User;
import com.photography.service.IProjectOrderService;
import com.photography.service.IProjectService;
import com.photography.utils.Constants;
import com.photography.utils.CustomizedPropertyPlaceholderConfigurer;
import com.photography.utils.FileUtil;
import com.photography.utils.MessageConstants;

/**
 * 活动
 * @author 徐雁斌
 * @since 2015-3-13
 * 
 * @copyright 2015 天大求实电力新技术股份有限公司 版权所有
 */
@Controller
@RequestMapping("/project")
public class ProjectController extends BaseController {
	
	private final static Logger log = Logger.getLogger(ProjectController.class);
	
	public final static String PROJECT_FILE = "user.project.file";
	
	@Autowired
	private IProjectService projectService;
	
	@Autowired
	private IProjectOrderService projectOrderService;
	
	public void setProjectService(IProjectService projectService) {
		this.projectService = projectService;
	}
	
	public void setProjectOrderService(IProjectOrderService projectOrderService) {
		this.projectOrderService = projectOrderService;
	}

	/**
	 * 进入新建页面
	 * @param request
	 * @param model
	 * @return
	 * @author 徐雁斌
	 */
	@RequestMapping(value="/toCreate")
	public ModelAndView toCreate(HttpServletRequest request, Model model){
		ModelAndView mav = new ModelAndView();
		mav.setViewName("project/project_create_modal");
		return mav;
	}

	/**
	 * 新建活动
	 * @param project
	 * @param imgFiles
	 * @param request
	 * @param model
	 * @return
	 * @author 徐雁斌
	 */
	@RequestMapping(value="/create")
	public ModelAndView create(Project project,@RequestParam MultipartFile[] photoPics,@RequestParam MultipartFile[] modelPics,HttpServletRequest request, Model model){
		ModelAndView mav = new ModelAndView();
		mav.setViewName("project/project_create");
		try{
			User user = (User) request.getSession().getAttribute(Constants.SESSION_USER_KEY);
			if(user == null){
				mav.addObject("project", project);
				mav.addObject("errorMessage", ErrorMessage.get(ErrorCode.SESSION_TIMEOUT));
				return mav;
			}
			
			Project projectDb = null;
			if(project.getId() != null && !"".equals(project.getId())){
				projectDb = (Project) projectService.loadPojo(Project.class,project.getId());
			}
			
        	FileGroup photoGroup = null;
        	if(projectDb != null){
        		photoGroup = projectDb.getPhotos();
        	}
        	photoGroup = saveAndReturnFile(photoPics, request, user, photoGroup);
        	project.setPhotos(photoGroup);
        	
        	FileGroup modelPhotoGroup = null;
        	if(projectDb != null){
        		modelPhotoGroup = projectDb.getModelPhotos();
        	}
        	modelPhotoGroup = saveAndReturnFile(modelPics, request, user, modelPhotoGroup);
        	project.setModelPhotos(modelPhotoGroup);
        	
        	project.setCreateUser(user);
        	
        	projectService.savePojo(project, user);
        	mav.addObject("project", project);
			
		}catch(Exception e){
			if(e instanceof ServiceException){
				ServiceException se = (ServiceException) e;
				mav.addObject("errorMessage", se.getErrorMessage()); 
				mav.setViewName("error/error");
			}else{
				log.error("error",e);
				mav.addObject("error_message", ErrorMessage.get(ErrorCode.UNKNOWN_ERROR));
				mav.setViewName("error/error");
			}
		}
		return mav;
	}

	/**
	 * @param filePath     文件保存路径
	 * @param relativePath 文件相对路径
	 * @param files        文件对象
	 * @return
	 * @throws Exception
	 * @author 徐雁斌
	 */
	private FileGroup saveAndReturnFile(MultipartFile[] files,HttpServletRequest request,User user,FileGroup fileGroup) throws Exception {
		
		if(fileGroup == null){
			fileGroup = new FileGroup();
		}
		//绝对路径
		String filePath = request.getSession().getServletContext().getRealPath((String)
    			CustomizedPropertyPlaceholderConfigurer.getContextProperty(PROJECT_FILE))  + File.separator + user.getEmail();       	
    	//相对路径
    	String relativePath = CustomizedPropertyPlaceholderConfigurer.getContextProperty(PROJECT_FILE) + user.getEmail();
  
    	
		for(int i=0;i<files.length;i++){
			MultipartFile file =  files[i];
			if(file.isEmpty()){
				continue;
			}
			
			FileInfo fileInfo = new FileInfo();
			fileInfo.setExt(FileUtil.getFileExtension(file.getOriginalFilename()));
			fileInfo.setRealName(file.getOriginalFilename());
			String fileName = UUID.randomUUID() + "." + fileInfo.getExt();
			fileInfo.setRealPath(relativePath + "/" + fileName);
			fileInfo.setFileGroup(fileGroup);
			
			fileGroup.getFileInfos().add(fileInfo);
			
			
			FileUtil.saveFileByName(filePath, file, fileName);
		}
		
		projectService.savePojo(fileGroup, user);
		
		return fileGroup;
	}
	
	/**
	 * 进入新建页面
	 * @param request
	 * @param model
	 * @return
	 * @author 徐雁斌
	 */
	@RequestMapping(value="/toReview")
	public ModelAndView toReview(String id,HttpServletRequest request, Model model){
		ModelAndView mav = new ModelAndView();
		return reviewProject(id, request, mav);
	}

	/**
	 * @param id
	 * @param request
	 * @param mav
	 * @return
	 * @author 徐雁斌
	 */
	private ModelAndView reviewProject(String id, HttpServletRequest request, ModelAndView mav) {
		
		mav.setViewName("project/project_review");
		
		//查找浏览的活动
		Project project = (Project) projectService.loadPojo(Project.class,id);
		if(project == null){
			mav.addObject("errorMessage", ErrorMessage.get(ErrorCode.PROJECT_NOT_EXIST));
		}
		mav.addObject("project", project);
		
		//根据用户确定是否显示预定按钮
		User user = (User) request.getSession().getAttribute(Constants.SESSION_USER_KEY);
		if(user == null){
			mav.addObject("errorMessage", ErrorMessage.get(ErrorCode.SESSION_TIMEOUT));
			return mav;
		}
		mav.addObject("isCanYd",projectOrderService.isCanYd(user.getId(), id));
		
		try {
			mav.addObject("rela_projects", projectService.getRelaProject(id));
		} catch (ServiceException e) {
			log.error("ProjectController.toReview(): ServiceException", e);
		}
		
		//更新浏览次数
		String viewNumber = project.getViewedNumber();
		if(viewNumber == null || "".equals(viewNumber)){
			viewNumber = "0";
		}
		project.setViewedNumber(Integer.toString(Integer.parseInt(viewNumber) + 1));
		try {
			projectService.savePojo(project, null);
		} catch (ServiceException e) {
			log.error("ProjectController.toReview(): ServiceException", e);
		}
		
		return mav;
	}
	
	/**
	 * 预定活动
	 * @param id
	 * @param request
	 * @param model
	 * @return
	 * @author 徐雁斌
	 */
	@RequestMapping(value="/orderProject")
	public ModelAndView orderProject(String id,HttpServletRequest request, Model model){
		ModelAndView mav = new ModelAndView();
		//根据用户确定是否显示预定按钮
		User user = (User) request.getSession().getAttribute(Constants.SESSION_USER_KEY);
		if(user == null){
			return reviewProject(id, request, mav);
		}
		
		try {
			projectOrderService.saveOrderProject(user.getId(), id);
			mav.addObject("successMessage", MessageConstants.ORDER_SUCCESS);
		} catch (ServiceException e) {
			log.error("ProjectController.orderProject(): ServiceException", e);
			mav.addObject("errorMessage", ErrorMessage.get(e.getErrorCode()));
		}
		
		return reviewProject(id, request, mav);
	}
	
	@RequestMapping(value="/test")
	public ModelAndView test(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		Project project = (Project) projectService.loadPojo(Project.class,"297ea9d44d31093d014d311c03cb000d");
		mv.addObject("project", project);
		mv.addObject("rela_projects",projectService.loadPojoByExpression(Project.class, null, null));
		mv.setViewName("project/project_review");
		return mv;
	}
	
	
}
