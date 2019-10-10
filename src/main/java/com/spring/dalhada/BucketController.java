package com.spring.dalhada;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import service.BucketService;
import service.NaverBlogService;
import service.SearchBucketService;
import vo.BucketDetailVO;
import vo.BucketVO;
import vo.LikeInfoVO;
import vo.NaverBlogVO;
import vo.PagingVO;
import vo.SearchBucketVO;
import vo.StringIntVO;
import vo.TagInfoVO;
import vo.SelectedBucketVO;

@Controller
public class BucketController {
	@Autowired
	private SearchBucketService searchBucketService;
	@Autowired
	private NaverBlogService naverBlogService;
	@Resource(name="bucketService")
	private BucketService bucketservice;

	
	@RequestMapping(value="/main")
	public ModelAndView main(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		String id = (String)session.getAttribute("id");
	
		List<BucketVO> TOPlist = bucketservice.selectTOPBucket(id);
		
		mav.addObject("TOPlist", TOPlist);
		mav.setViewName("main");
		return mav;
	}
	
	@RequestMapping(value="/main/like")
	@ResponseBody
	public String clickheart(HttpSession session, LikeInfoVO vo) {
		int result = 0;
		String id = (String)session.getAttribute("id");
		
		if(id != null) {
			vo.setMember_id(id);
			String action = vo.getAction();
			if(action.equals("insert")) {
				result = bucketservice.insertLikeInfo(vo);
			}else if(action.equals("delete")) {
				result = bucketservice.deleteLikeInfo(vo);
			}
		}else {
			result = -1;
		}
		return result+"";
	}
	
	@RequestMapping(value="/main/getgrouptag")
	@ResponseBody
	public List<List<StringIntVO>> getgroup(HttpSession session) {
		String member_id = (String)session.getAttribute("id");
		List<List<StringIntVO>> list = new ArrayList<List<StringIntVO>>();
		
		if(member_id!=null) {
			List<StringIntVO> groups = bucketservice.selectGroups(member_id);
			List<StringIntVO> tags = bucketservice.selectTags();
			list.add(groups);
			list.add(tags);
		}
		return list;
	}
	
	/*@RequestMapping(value="/searchbucket/get")
	@ResponseBody
	public BucketDetailVO groupmodal(HttpSession session, @RequestParam(required=false) String selectedbucket_id) {
	      String id = (String) session.getAttribute("id");
	      BucketDetailVO selectedBucketList = null;
	      int sid= Integer.parseInt(selectedbucket_id);
	      if(id != null) {
	         //가져오기 select
	         System.out.println("가져오기 버튼 눌렀을 때 selectedbucket_id값 : "+selectedbucket_id);
	 		 selectedBucketList = searchBucketService.selectSelectedBucket(sid);
	 		 System.out.println("selectedBucketList값 : "+selectedBucketList.toString());
	 		 selectedBucketList.setTags(searchBucketService.selectSelectedTag(sid));
	      }
	    return selectedBucketList;
	}*/      
	
	//좋아요  많은 거 / 추천 버킷
	@RequestMapping(value="/main/modaldetail")
	@ResponseBody
	public BucketDetailVO modaldetail(HttpSession session, String selectedbucket_id) {
		String member_id = (String) session.getAttribute("id");
		StringIntVO map = new StringIntVO();
		map.setId(Integer.parseInt(selectedbucket_id));
		map.setName(member_id);
		BucketDetailVO vo =  bucketservice.selectDetail(map);
		return vo;
	}

	@RequestMapping(value="/createbucket")
	@ResponseBody
	public String createbucket(HttpSession session, SelectedBucketVO vo, @RequestParam(value="g_id")String g_id,
			@RequestParam(value="taglist")List<String> taglist, @RequestParam("file") MultipartFile file) {
		String fileName = file.getOriginalFilename(), filePath;
		String member_id = (String) session.getAttribute("id");
		vo.setMember_id(member_id);
		vo.setTag_id(taglist);
		vo.setGroup_id(Integer.parseInt(g_id));
		vo.setImage_path("_"+fileName);
		filePath = bucketservice.insertBucket(vo);
		byte[] image = null;
		try {
			image = file.getBytes();
			File f = new File("C:/unico/eclipse-workspace/Dalhada/src/main/webapp/resources/images/bucket/"+filePath);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(image);
			fos.close();
			File newf = new File("C:/unico/eclipse-workspace/Dalhada/src/main/webapp/resources/images/bucket/"+filePath);
	    	 if(f.exists())
	    		 f.renameTo(newf);
		}catch (IOException e) {
	    	 e.printStackTrace();
	     }	
		return "success";
	}
	
	@RequestMapping(value="/searchbucket")
	public ModelAndView searchBucket(HttpSession session, @RequestParam(defaultValue="1")int curPage, 
			@ModelAttribute SearchBucketVO searchBucketVO) {
		ModelAndView mav = new ModelAndView();
		String id = (String) session.getAttribute("id");
		searchBucketVO.setMember_id(id);
		String keyword = searchBucketVO.getSearchKeyword();
		String tagName = searchBucketVO.getSearchTag();
		String naverKeyword = null;
		List<NaverBlogVO> naverBlogList = null;
		int listCnt;
		PagingVO pageList;
		List<BucketVO> searchList;

		if(tagName != null) {
			//태그검색 검색결과 수&페이징
			listCnt = searchBucketService.getTotalTagCnt(tagName);
			pageList = new PagingVO(listCnt, curPage); //(전체 게시물 수, 현재 페이지)
			searchBucketVO.setStartRow(pageList.getStartIndex());
			searchBucketVO.setEndRow(pageList.getEndIndex());
			mav.addObject("listCnt", listCnt);
			mav.addObject("pagination", pageList);
			
			//태그검색
			searchBucketVO.setSearchTag(tagName);
			searchList = searchBucketService.searchTag(searchBucketVO);
			for(BucketVO vo: searchList) {
				vo.addClass();
			}
			mav.addObject("searchList", searchList);
			mav.addObject("keyword", "");
		}else {
			//제목검색 검색결과 수&페이징
			listCnt = searchBucketService.getTotalTitleCnt(keyword);
			pageList = new PagingVO(listCnt, curPage); //(전체 게시물 수, 현재 페이지)
			searchBucketVO.setStartRow(pageList.getStartIndex());
			searchBucketVO.setEndRow(pageList.getEndIndex());
			mav.addObject("listCnt", listCnt);
			mav.addObject("pagination", pageList);
			
			//제목검색
			searchList = searchBucketService.searchTitle(searchBucketVO);
			for(BucketVO vo: searchList) {
				vo.addClass();
			}
			
			mav.addObject("searchList", searchList);
			mav.addObject("keyword", keyword);
		}
		
		//태그명 찾기
		List<TagInfoVO> tagNameList = searchBucketService.selectTagName();
		mav.addObject("tagNameList", tagNameList);
		
		//그룹명 찾기
		//List<GroupVO> groupNameList = searchBucketService.selectGroupName(id);
		//mav.addObject("groupNameList", groupNameList);
		
		//네이버 블로그 검색결과
		if(tagName == null) {
			naverKeyword = (keyword == null || keyword.equals("") || searchList.isEmpty()) ? "버킷리스트": keyword;
		}else {
			naverKeyword = (tagName.equals("기타")) ? "버킷리스트": tagName;
		}
		naverBlogList = naverBlogService.selectNaverBlog(naverKeyword,5,1);
		mav.addObject("naverBlogList", naverBlogList);
		
		mav.setViewName("searchbucket");
		return mav;
	}
}
