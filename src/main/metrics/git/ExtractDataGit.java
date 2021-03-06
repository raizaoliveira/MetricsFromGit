package main.metrics.git;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class ExtractDataGit {
	
	private static Git gitReference;
	private List<String> totalFilesChanges = new ArrayList<String>();
	private List<String> allListTags = new ArrayList<String>();
	private List<String> allDates = new ArrayList<String>();
	private int numberOfCommits;
	private int totalDifferences;
	private String firstdate;
    private String lastdate;
    private String projectID;
    private String savePath;
	
	public ExtractDataGit(String projectPath, String savePath){
		try {
			
			try {
				gitReference = Git.open(new File(projectPath));
				gitReference.checkout().setName("distrotech-libssh").call();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		this.projectID = projectPath;
		this.savePath = savePath;
		totalDifferences = 0;
		try {
			allReleases();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public List<String> getReleases() {
		return allListTags;
	}
	
	public void allReleases() throws Exception {
		String lastcommit = null;
		int i = 0;
		try {
			List<Ref> tagRefs = gitReference.tagList().call();
	    	for(Ref ref : tagRefs) {
	            String versionNumber = ref.getName().split("/")[2];
	            String commitId = ref.getObjectId().getName();
	            allListTags.add(versionNumber);
	            if(i > 0) {
	            	totalFilesChanges = diffFilesInCommits(lastcommit, commitId);
	            }
	            lastcommit = commitId;
	                     
	            
	            i++;
	            
	        }
	    } catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setDates();
		createMetricsTxt();
	
	}
	
	
	
	public void setDates() throws Exception{
			String treeName = "refs/heads/distrotech-libssh";
			Repository repo = gitReference.getRepository();
            Iterable<RevCommit> commits = gitReference.log().add(repo.resolve(treeName)).call();
            int count = 0;
            PersonIdent authorIdent1, authorIdent2;
        	Date firstDate = null, lastDate = null;
        	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
        	
            for (RevCommit commit : commits) {
            	
            	if(count == 0) {
            		authorIdent2 = commit.getAuthorIdent();
            		lastDate = authorIdent2.getWhen();
            	}else {
            		authorIdent1 = commit.getAuthorIdent();
            		firstDate = authorIdent1.getWhen();
            		allDates.add(DATE_FORMAT.format(firstDate));
            	}
            	
                count++;
            }
           
            firstdate = DATE_FORMAT.format(firstDate);
            lastdate = DATE_FORMAT.format(lastDate);
            numberOfCommits = count;

    }
	
	
	public List<String> diffFilesInCommits(String oldCommitId, String newCommitId) throws GitAPIException, IOException{
		List<DiffEntry> diffs = gitReference.diff()
                .setOldTree(prepareTreeParser(gitReference.getRepository(), oldCommitId))
                .setNewTree(prepareTreeParser(gitReference.getRepository(), newCommitId))
                .call();
        totalDifferences += diffs.size();
        List<String> modifiedFiles = new ArrayList<String>();
        for (DiffEntry diff : diffs) {
            //checking for .c file
            if(diff.getNewPath().substring(diff.getNewPath().length()-2,diff.getNewPath().length()).equals(".c")){
            	//adding diff.getNewPath() we have the entire new path to the file
            	modifiedFiles.add(diff.getNewPath());
            }
        }
        return modifiedFiles;
	}
	
	
	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
		try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }
	
	public static String getFileFromPath(String path){
		String fileToReturn = "";
		for(int length = path.length()-1 ; length >= 0 ; length--){
			if(path.charAt(length) != '/'){
				fileToReturn = fileToReturn + path.charAt(length);
			}
			else{
				break;
			}
		}
		return new StringBuilder(fileToReturn).reverse().toString();
		
	}
	
	
	public void createMetricsTxt(){
		System.out.println("METRICS");
		try {
			new File(savePath).mkdirs();
			FileWriter arq = new FileWriter(savePath+"\\"+"teste"+".csv");
			PrintWriter gravarArq = new PrintWriter(arq);
			gravarArq.println("Primeiro Commit,Ultimo Commit,Quantidade de Releases,Quantidade de Commits,Total de Arquivos Modificados,Total de Diferen�as");
			gravarArq.println(firstdate+","+lastdate+","+allListTags.size()+","+numberOfCommits+","+totalFilesChanges.size()+","+totalDifferences);
			Collections.reverse(allDates);
			for (String data: allDates) {
				gravarArq.println(data);
			}
			arq.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	


}
