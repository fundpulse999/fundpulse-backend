package com.fundpulse.app.service;

import com.fundpulse.app.dto.proposalProgressForm;
import com.fundpulse.app.models.Proposal;
import com.fundpulse.app.repositories.ProposalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TempInvestmentService {



    @Autowired
    private ProposalRepo proposalRepository;

    public String investInProposal(String investorId, String proposalId, Long amount) {
        Optional<Proposal> optionalProposal = proposalRepository.findById(proposalId);

        if (optionalProposal.isEmpty()) {
            return "Proposal not found!";
        }

        Proposal proposal = optionalProposal.get();

        // Ensure proposal is still active
        if (proposal.isStatus() == false || proposal.getEndDate().isBefore(LocalDateTime.now())) {
            return "Investment closed for this proposal!";
        } 

        // Ensure investment meets minimum amount
        if (amount < proposal.getMinInvestment()) {
            return "Minimum investment required: " + proposal.getMinInvestment();
        }

        // Ensure investment does not exceed funding goal
        long remainingAmount = proposal.getAmountToRaise() - proposal.getRaisedAmount();
        if (amount > remainingAmount) {
            return "Only " + remainingAmount + " is required to complete funding!";
        }

        // Save investment
//        Investment investment = new Investment(null, proposalId, investorId, amount, LocalDateTime.now());
//        investmentRepository.save(investment);

        // Update proposal's raised amount
        proposal.setRaisedAmount(proposal.getRaisedAmount() + amount);
        proposalRepository.save(proposal);

        return "Investment successful!";
    }

//    public List<Investment> getInvestmentsByProposal(String proposalId) {
//        return investmentRepository.findByProposalId(proposalId);
//    }

    public proposalProgressForm getProposalProgress(String proposalId) {
        Optional<Proposal> optionalProposal = proposalRepository.findById(proposalId);

        if (optionalProposal.isEmpty()) {
            return null; // Handle this in the controller with an error response
        }

        Proposal proposal = optionalProposal.get();
        long remainingAmount = proposal.getAmountToRaise() - proposal.getRaisedAmount();

        return new proposalProgressForm(
                proposal.getProposalId(),
                proposal.getAmountToRaise(),
                proposal.getRaisedAmount(),
                remainingAmount,
                proposal.getMinInvestment(),
                proposal.getStartDate(),
                proposal.getEndDate()
        );
    }
}
